package com.msa.msahub.features.devices.data.remote.mqtt

import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttMessage
import com.msa.msahub.core.platform.network.mqtt.Qos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class DeviceMqttHandler(
    private val mqttClient: MqttClient
) {
    suspend fun subscribeToState(deviceId: String) {
        mqttClient.subscribe(DeviceMqttTopics.statusTopic(deviceId), qos = Qos.AtLeastOnce)
    }

    fun observeState(deviceId: String): Flow<DeviceMqttEvent> {
        val topic = DeviceMqttTopics.statusTopic(deviceId)
        return mqttClient.incomingMessages
            .filter { it.topic == topic }
            .map { message ->
                parseStatusMessage(deviceId, message) ?: DeviceStatusEvent(
                    deviceId = deviceId,
                    online = true,
                    state = emptyMap(),
                    timestamp = System.currentTimeMillis()
                )
            }
    }

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

        // 1) اگر فرمت دقیقا مطابق DeviceStatusEvent بود
        DeviceMqttTopicParser.parse(message.topic, jsonStr)?.let { ev ->
            return ev as? DeviceStatusEvent
        }

        // 2) fallback: JSON آزاد → تبدیل به state map
        return runCatching {
            val json = JSONObject(jsonStr)

            val deviceId = json.optString("deviceId").takeIf { it.isNotBlank() } ?: deviceIdFromTopic
            val online = when {
                json.has("online") -> json.optBoolean("online", true)
                json.has("isOnline") -> json.optBoolean("isOnline", true)
                else -> true
            }

            val ts =
                json.optLong("timestamp",
                    json.optLong("updatedAt", System.currentTimeMillis())
                )

            // اگر state به صورت nested object بود، merge کن
            val stateObj = if (json.has("state") && !json.isNull("state")) json.optJSONObject("state") else null

            val flat = mutableMapOf<String, String>()
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                if (k in setOf("deviceId", "online", "isOnline", "timestamp", "updatedAt", "state")) continue
                flat[k] = json.opt(k)?.toString() ?: continue
            }

            if (stateObj != null) {
                val sk = stateObj.keys()
                while (sk.hasNext()) {
                    val k = sk.next()
                    flat[k] = stateObj.opt(k)?.toString() ?: continue
                }
            }

            DeviceStatusEvent(
                deviceId = deviceId,
                online = online,
                state = flat,
                timestamp = ts
            )
        }.getOrNull()
    }
}
