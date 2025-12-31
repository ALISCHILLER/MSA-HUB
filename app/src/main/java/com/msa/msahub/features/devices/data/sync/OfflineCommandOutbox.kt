package com.msa.msahub.features.devices.data.sync

import android.util.Base64
import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Clock
import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.common.Result
import com.msa.msahub.core.platform.network.mqtt.Qos
import com.msa.msahub.features.devices.data.local.dao.OfflineCommandDao
import com.msa.msahub.features.devices.data.local.entity.CommandStatus
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandEntity
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler

/**
 * پیاده‌سازی صنعتی Outbox Pattern برای تضمین ارسال فرمان‌ها با مدیریت وضعیت و تکرار.
 */
class OfflineCommandOutbox(
    private val dao: OfflineCommandDao,
    private val mqttHandler: DeviceMqttHandler,
    private val clock: Clock,
    private val logger: Logger
) {

    suspend fun flush(max: Int = 50): Result<Int> {
        return try {
            val pending = dao.getPendingCommands(limit = max)
            if (pending.isEmpty()) return Result.Success(0)

            logger.d("Flushing ${pending.size} commands from outbox")
            
            // ۱. تغییر وضعیت به SENDING برای جلوگیری از تداخل Workerها
            val ids = pending.map { it.id }
            dao.updateStatus(ids, CommandStatus.SENDING, clock.currentTimeMillis())

            var sentCount = 0

            for (cmd in pending) {
                val result = runCatching { publish(cmd) }
                val now = clock.currentTimeMillis()

                if (result.isSuccess) {
                    dao.markAsSent(cmd.id, now)
                    sentCount++
                } else {
                    val error = result.exceptionOrNull()
                    logger.e("Failed to send command ${cmd.id}", error)
                    
                    if (cmd.attempts + 1 >= cmd.maxAttempts) {
                        dao.markAsPermanentlyFailed(cmd.id, error?.message, now)
                    } else {
                        dao.markAsFailedAndRetry(cmd.id, error?.message, now)
                    }
                }
            }

            Result.Success(sentCount)
        } catch (t: Throwable) {
            logger.e("Critical failure during outbox flush", t)
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
