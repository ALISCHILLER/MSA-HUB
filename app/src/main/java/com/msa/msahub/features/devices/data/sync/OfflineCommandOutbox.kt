package com.msa.msahub.features.devices.data.sync

import android.util.Base64
import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Result
import com.msa.msahub.core.platform.network.mqtt.Qos
import com.msa.msahub.features.devices.data.local.dao.OfflineCommandDao
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandEntity
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler
import timber.log.Timber

class OfflineCommandOutbox(
    private val dao: OfflineCommandDao,
    private val mqttHandler: DeviceMqttHandler
) {

    /**
     * Tries to send up to [max] queued commands.
     * Implements retry logic and industrial error handling.
     */
    suspend fun flush(max: Int = 50): Result<Int> {
        return try {
            val pending = dao.getPending(limit = max, maxAttempts = MAX_ATTEMPTS)
            var sent = 0

            for (cmd in pending) {
                val result = runCatching { publish(cmd) }
                
                if (result.isSuccess) {
                    dao.deleteById(cmd.id)
                    sent++
                } else {
                    val error = result.exceptionOrNull()
                    Timber.e(error, "Failed to flush command ${cmd.id}")
                    
                    // Industrial Outbox: Update attempts and last error instead of just ignoring
                    dao.update(
                        cmd.copy(
                            attempts = cmd.attempts + 1,
                            lastError = error?.message ?: "Unknown MQTT error"
                        )
                    )
                }
            }

            Result.Success(sent)
        } catch (t: Throwable) {
            Timber.e(t, "Critical failure during outbox flush")
            Result.Failure(AppError.Mqtt("Failed to flush offline outbox", t))
        }
    }

    private suspend fun publish(cmd: OfflineCommandEntity) {
        val payload = Base64.decode(cmd.payloadBase64, Base64.NO_WRAP)
        mqttHandler.publishCommand(
            topic = cmd.topic,
            payload = payload,
            qos = intToQos(cmd.qos),
            retained = cmd.retained
        )
    }

    private fun intToQos(qos: Int): Qos = when (qos) {
        0 -> Qos.AtMostOnce
        2 -> Qos.ExactlyOnce
        else -> Qos.AtLeastOnce
    }

    companion object {
        private const val MAX_ATTEMPTS = 5
    }
}
