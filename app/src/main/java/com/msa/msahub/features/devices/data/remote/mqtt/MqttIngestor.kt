package com.msa.msahub.features.devices.data.remote.mqtt

import com.msa.msahub.core.common.IdGenerator
import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject

/**
 * مسئول گوش دادن به پیام‌های MQTT و ذخیره خودکار آن‌ها در دیتابیس (Room)
 */
class MqttIngestor(
    private val mqttClient: MqttClient,
    private val deviceStateDao: DeviceStateDao,
    private val ids: IdGenerator,
    private val scope: CoroutineScope,
    private val logger: Logger
) {
    fun start() {
        logger.i("MQTT Ingestor starting...")
        
        mqttClient.incomingMessages
            .onEach { message ->
                val deviceId = DeviceMqttTopics.extractDeviceId(message.topic)
                if (deviceId != null) {
                    when {
                        message.topic.endsWith("/status") -> handleStatusMessage(deviceId, message.payload)
                        message.topic.endsWith("/ack") -> logger.d("Command ACK received for $deviceId")
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
                isOn = json.optBoolean("on", false),
                brightness = json.optInt("brightness", 0).takeIf { json.has("brightness") },
                temperatureC = json.optDouble("temp", 0.0).takeIf { json.has("temp") },
                humidityPercent = json.optInt("humidity", 0).takeIf { json.has("humidity") },
                batteryPercent = json.optInt("battery", 0).takeIf { json.has("battery") },
                updatedAtMillis = System.currentTimeMillis()
            )
            
            deviceStateDao.upsert(stateEntity)
            logger.d("Status updated for device: $deviceId")
        } catch (e: Exception) {
            logger.e("Failed to parse status payload for $deviceId", e)
        }
    }
}
