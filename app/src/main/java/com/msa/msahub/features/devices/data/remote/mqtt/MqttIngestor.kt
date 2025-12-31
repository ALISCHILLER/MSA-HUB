package com.msa.msahub.features.devices.data.remote.mqtt

import com.msa.msahub.core.common.IdGenerator
import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionState
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONObject

class MqttIngestor(
    private val mqttClient: MqttClient,
    private val deviceStateDao: DeviceStateDao,
    private val ids: IdGenerator,
    private val scope: CoroutineScope,
    private val logger: Logger
) {
    fun start() {
        logger.i("MQTT Ingestor starting...")

        // ۱. مدیریت اشتراک‌های خودکار به محض اتصال
        scope.launch {
            mqttClient.connectionState
                .filter { it is MqttConnectionState.Connected }
                .collect {
                    runCatching {
                        mqttClient.subscribe(DeviceMqttTopics.ALL_DEVICES_STATUS)
                        mqttClient.subscribe(DeviceMqttTopics.ALL_DEVICES_ACK)
                        logger.i("Subscribed to status and ack topics")
                    }.onFailure { e ->
                        logger.e("Subscription failed", e)
                    }
                }
        }

        // ۲. پردازش پیام‌های دریافتی
        mqttClient.incomingMessages
            .onEach { message ->
                val deviceId = DeviceMqttTopics.extractDeviceId(message.topic)
                if (deviceId != null) {
                    when {
                        message.topic.endsWith("/status") -> handleStatusMessage(deviceId, message.payload)
                        message.topic.endsWith("/ack") -> logger.d("ACK received for $deviceId")
                    }
                }
            }
            .launchIn(scope)
    }

    private suspend fun handleStatusMessage(deviceId: String, payload: ByteArray) {
        try {
            val json = JSONObject(String(payload))
            val stateEntity = DeviceStateEntity(
                id = ids.uuid(),
                deviceId = deviceId,
                isOnline = json.optBoolean("online", true),
                isOn = if (json.has("on")) json.getBoolean("on") else null,
                brightness = if (json.has("brightness")) json.getInt("brightness") else null,
                temperatureC = if (json.has("temp")) json.getDouble("temp") else null,
                humidityPercent = if (json.has("humidity")) json.getDouble("humidity") else null,
                batteryPercent = if (json.has("battery")) json.getInt("battery") else null,
                updatedAtMillis = System.currentTimeMillis()
            )
            deviceStateDao.upsert(stateEntity)
        } catch (e: Exception) {
            logger.e("Parse error for $deviceId", e)
        }
    }
}
