package com.msa.msahub.features.devices.data.sync

import android.util.Base64
import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Result
import com.msa.msahub.core.platform.network.mqtt.Qos
import com.msa.msahub.features.devices.data.local.dao.OfflineCommandDao
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandEntity
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler

class OfflineCommandOutbox(
    private val dao: OfflineCommandDao,
    private val mqttHandler: DeviceMqttHandler
) {

    /**
     * Tries to send up to [max] queued commands.
     * Returns number of successfully sent commands.
     */
    suspend fun flush(max: Int = 50): Result<Int> {
        return try {
            val all = dao.getAll().take(max)
            var sent = 0

            for (cmd in all) {
                val ok = runCatching { publish(cmd) }.isSuccess
                if (ok) {
                    dao.deleteById(cmd.id)
                    sent++
                } else {
                    // keep it in outbox; optionally you can implement attempts++ update later
                }
            }

            Result.Success(sent)
        } catch (t: Throwable) {
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
}
