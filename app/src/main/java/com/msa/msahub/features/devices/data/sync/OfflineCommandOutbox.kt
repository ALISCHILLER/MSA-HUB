package com.msa.msahub.features.devices.data.sync

import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Result
import com.msa.msahub.core.platform.network.mqtt.Qos
import com.msa.msahub.features.devices.data.local.dao.OfflineCommandDao
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandStatus
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler
import timber.log.Timber

class OfflineCommandOutbox(
    private val dao: OfflineCommandDao,
    private val mqttHandler: DeviceMqttHandler
) {
    suspend fun flush(max: Int = 50): Result<Int> {
        return try {
            val ready = dao.getReadyCommands(limit = max)
            var processed = 0
            val now = System.currentTimeMillis()

            for (cmd in ready) {
                val locked = dao.markSendingIfPending(cmd.id, now)
                if (locked == 0) continue

                val publishResult = runCatching {
                    mqttHandler.publishCommand(
                        topic = cmd.topic,
                        payload = android.util.Base64.decode(cmd.payloadBase64, android.util.Base64.NO_WRAP),
                        qos = when(cmd.qos) {
                            0 -> Qos.AtMostOnce
                            2 -> Qos.ExactlyOnce
                            else -> Qos.AtLeastOnce
                        },
                        retained = cmd.retained
                    )
                }

                if (publishResult.isSuccess) {
                    dao.markAsSent(cmd.id, now)
                    processed++
                } else {
                    val e = publishResult.exceptionOrNull()
                    Timber.e(e, "Outbox publish failed id=${cmd.id}")

                    if (cmd.attempts + 1 >= cmd.maxAttempts) {
                        dao.markPermanentlyFailed(cmd.id, e?.message ?: "Unknown", now)
                    } else {
                        dao.markFailedAndRetry(cmd.id, e?.message ?: "Unknown", now)
                    }
                }
            }

            Result.Success(processed)
        } catch (t: Throwable) {
            Timber.e(t, "Critical failure during outbox flush")
            Result.Failure(AppError.Mqtt("Failed to flush offline outbox", t))
        }
    }
}
