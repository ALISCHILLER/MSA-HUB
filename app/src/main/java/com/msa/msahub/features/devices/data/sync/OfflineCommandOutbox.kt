package com.msa.msahub.features.devices.data.sync

import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Result
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionState
import com.msa.msahub.core.platform.network.mqtt.Qos
import com.msa.msahub.features.devices.data.local.dao.OfflineCommandDao
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler
import timber.log.Timber
import kotlin.math.pow

class OfflineCommandOutbox(
    private val dao: OfflineCommandDao,
    private val mqttHandler: DeviceMqttHandler,
    private val mqttClient: MqttClient
) {
    suspend fun flush(max: Int = 50): Result<Int> {
        if (mqttClient.connectionState.value !is MqttConnectionState.Connected) {
            Timber.d("Outbox flush skipped: MQTT not connected")
            return Result.Success(0)
        }

        return try {
            val now = System.currentTimeMillis()
            val ready = dao.getReadyCommands(limit = max, now = now)
            var processed = 0

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
                        dao.markPermanentlyFailed(cmd.id, e?.message ?: "Max attempts reached", now)
                    } else {
                        // محاسبه زمان تلاش بعدی با الگوریتم Exponential Backoff
                        // فواصل: 5s, 25s, 125s, 625s...
                        val backoffSeconds = 5.0.pow(cmd.attempts.toDouble() + 1).toLong()
                        val nextRetryAt = now + (backoffSeconds * 1000L)
                        
                        dao.markFailedAndRetry(cmd.id, e?.message ?: "Transient failure", nextRetryAt, now)
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
