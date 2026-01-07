package com.msa.msahub.features.devices.data.mapper

import android.util.Base64
import com.msa.msahub.core.platform.network.mqtt.Qos
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandEntity
import com.msa.msahub.features.devices.domain.model.DeviceCommand
import org.json.JSONObject

/**
 * مسئول تبدیل مدل‌های فرمان به فرمت‌های قابل انتقال و ذخیره‌سازی.
 * قانون طلایی: همیشه یک commandId منحصر‌به‌فرد برای جلوگیری از اجرای تکراری (Idempotency) وجود دارد.
 */
class DeviceCommandMapper {

    fun toMqttPayload(command: DeviceCommand): ByteArray {
        val obj = JSONObject().apply {
            // استفاده از commandId برای Idempotency سمت دستگاه
            put("commandId", command.commandId) 
            put("deviceId", command.deviceId)
            put("action", command.action)
            put("createdAt", command.createdAtMillis)
            put("params", JSONObject(command.params))
        }
        return obj.toString().toByteArray(Charsets.UTF_8)
    }

    fun toOfflineEntity(
        id: String,
        deviceId: String,
        topic: String,
        payload: ByteArray,
        qos: Qos,
        retained: Boolean,
        createdAtMillis: Long,
        correlationId: String? = null
    ): OfflineCommandEntity {
        return OfflineCommandEntity(
            id = id,
            deviceId = deviceId,
            topic = topic,
            payloadBase64 = Base64.encodeToString(payload, Base64.NO_WRAP),
            qos = qosToInt(qos),
            retained = retained,
            createdAtMillis = createdAtMillis,
            correlationId = correlationId ?: id,
            attempts = 0,
            lastError = null,
            nextRetryAtMillis = 0 // برای تلاش اول بلافاصله آماده است
        )
    }

    private fun qosToInt(qos: Qos): Int = when (qos) {
        Qos.AtMostOnce -> 0
        Qos.AtLeastOnce -> 1
        Qos.ExactlyOnce -> 2
    }
}
