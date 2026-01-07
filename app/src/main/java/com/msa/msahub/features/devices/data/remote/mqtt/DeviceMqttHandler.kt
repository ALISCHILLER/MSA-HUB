package com.msa.msahub.features.devices.data.remote.mqtt

import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttMessage
import com.msa.msahub.core.platform.network.mqtt.Qos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import timber.log.Timber

/**
 * مدیریت‌کننده پیام‌های MQTT برای دستگاه‌ها.
 * وظیفه تبدیل پیام‌های خام MQTT به رویدادهای دامنه و بالعکس را دارد.
 */
class DeviceMqttHandler(
    private val mqttClient: MqttClient
) {
    /**
     * سابسکرایب به وضعیت یک دستگاه خاص.
     */
    suspend fun subscribeToState(deviceId: String) {
        val topic = DeviceMqttTopics.statusTopic(deviceId)
        try {
            mqttClient.subscribe(topic, qos = Qos.AtLeastOnce)
            Timber.d("Subscribed to state: $topic")
        } catch (e: Exception) {
            Timber.e(e, "Failed to subscribe to status topic for $deviceId")
        }
    }

    /**
     * آن‌سابسکرایب از وضعیت یک دستگاه (برای بهینه‌سازی ترافیک).
     */
    suspend fun unsubscribeFromState(deviceId: String) {
        val topic = DeviceMqttTopics.statusTopic(deviceId)
        try {
            mqttClient.unsubscribe(topic)
            Timber.d("Unsubscribed from state: $topic")
        } catch (e: Exception) {
            Timber.e(e, "Failed to unsubscribe from status topic for $deviceId")
        }
    }

    /**
     * مشاهده تغییرات وضعیت یک دستگاه به صورت جریانی (Flow).
     */
    fun observeState(deviceId: String): Flow<DeviceMqttEvent> {
        val topic = DeviceMqttTopics.statusTopic(deviceId)
        return mqttClient.incomingMessages
            .filter { it.topic == topic }
            .map { message ->
                parseStatusMessage(deviceId, message) ?: createDefaultOnlineEvent(deviceId)
            }
    }

    /**
     * انتشار فرمان (Publish) بر روی شبکه MQTT.
     */
    suspend fun publishCommand(topic: String, payload: ByteArray, qos: Qos, retained: Boolean) {
        mqttClient.publish(
            MqttMessage(
                topic = topic,
                payload = payload,
                qos = qos,
                retained = retained
            )
        )
    }

    private fun parseStatusMessage(deviceIdFromTopic: String, message: MqttMessage): DeviceStatusEvent? {
        val jsonStr = runCatching { message.payload.toString(Charsets.UTF_8) }.getOrNull() ?: return null

        // ۱. تلاش برای استفاده از پارسر استاندارد پروژه
        DeviceMqttTopicParser.parse(message.topic, jsonStr)?.let { ev ->
            return ev as? DeviceStatusEvent
        }

        // ۲. منطق Fallback: پارس کردن دستی JSON آزاد برای سازگاری با انواع دستگاه‌ها
        return try {
            val json = JSONObject(jsonStr)

            val deviceId = json.optString("deviceId").takeIf { it.isNotBlank() } ?: deviceIdFromTopic
            val online = when {
                json.has("online") -> json.optBoolean("online", true)
                json.has("isOnline") -> json.optBoolean("isOnline", true)
                else -> true
            }

            val ts = json.optLong("timestamp", 
                        json.optLong("updatedAt", System.currentTimeMillis()))

            // استخراج وضعیت‌های داخلی (State)
            val flatState = mutableMapOf<String, String>()
            
            // الف) فیلدهای ریشه که متادیتا نیستند را به عنوان State در نظر بگیر
            val rootKeys = json.keys()
            val metaKeys = setOf("deviceId", "online", "isOnline", "timestamp", "updatedAt", "state", "type")
            while (rootKeys.hasNext()) {
                val k = rootKeys.next()
                if (k !in metaKeys) {
                    flatState[k] = json.opt(k)?.toString() ?: ""
                }
            }

            // ب) اگر یک آبجکت "state" وجود داشت، محتوای آن را Merge کن
            val nestedState = json.optJSONObject("state")
            if (nestedState != null) {
                val sk = nestedState.keys()
                while (sk.hasNext()) {
                    val k = sk.next()
                    flatState[k] = nestedState.opt(k)?.toString() ?: ""
                }
            }

            DeviceStatusEvent(
                deviceId = deviceId,
                online = online,
                state = flatState,
                timestamp = ts
            )
        } catch (e: Exception) {
            Timber.w("Failed to parse fallback JSON for device $deviceIdFromTopic: ${e.message}")
            null
        }
    }

    private fun createDefaultOnlineEvent(deviceId: String) = DeviceStatusEvent(
        deviceId = deviceId,
        online = true,
        state = emptyMap(),
        timestamp = System.currentTimeMillis()
    )
}
