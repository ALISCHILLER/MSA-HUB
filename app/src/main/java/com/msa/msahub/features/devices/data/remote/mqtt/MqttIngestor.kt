package com.msa.msahub.features.devices.data.remote.mqtt

import com.msa.msahub.core.common.IdGenerator
import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionState
import com.msa.msahub.features.devices.data.local.dao.DeviceHistoryDao
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.entity.DeviceHistoryEntity
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
    private val deviceHistoryDao: DeviceHistoryDao,
    private val ids: IdGenerator,
    private val scope: CoroutineScope,
    private val logger: Logger
) {

    fun start() {
        logger.i("MqttIngestor starting...")

        // Observe connection to re-subscribe if needed
        mqttClient.connectionState
            .onEach { state ->
                if (state is MqttConnectionState.Connected) {
                    logger.i("Connected. Subscribing to device statuses...")
                    // Subscribe to all device statuses
                    mqttClient.subscribe("devices/+/status")
                }
            }
            .launchIn(scope)

        // Process incoming messages
        mqttClient.incomingMessages
            .filter { it.topic.startsWith("devices/") && it.topic.endsWith("/status") }
            .onEach { msg ->
                // topic format: devices/{deviceId}/status
                val parts = msg.topic.split("/")
                if (parts.size == 3) {
                    val deviceId = parts[1]
                    handleStatusMessage(deviceId, msg.payload)
                }
            }
            .launchIn(scope)
    }

    private suspend fun handleStatusMessage(deviceId: String, payload: ByteArray) {
        try {
            val now = System.currentTimeMillis()
            val raw = payload.toString(Charsets.UTF_8)

            // 1) Prefer the standardized event model (kotlinx.serialization)
            val evt = DeviceStatusEvent.fromJson(raw)
            if (evt != null) {
                val state = evt.state
                val entity = DeviceStateEntity(
                    id = ids.uuid(),
                    deviceId = deviceId,
                    isOnline = evt.online,
                    isOn = parseBool(state, "on") ?: parseBool(state, "isOn"),
                    brightness = parseInt(state, "brightness"),
                    temperatureC = parseDouble(state, "temp") ?: parseDouble(state, "temperatureC"),
                    humidityPercent = parseDouble(state, "humidity") ?: parseDouble(state, "humidityPercent"),
                    batteryPercent = parseInt(state, "battery") ?: parseInt(state, "batteryPercent"),
                    updatedAtMillis = evt.timestamp
                )
                deviceStateDao.upsert(entity)

                // ✅ ثبت History
                deviceHistoryDao.insert(
                    DeviceHistoryEntity(
                        id = ids.uuid(),
                        deviceId = deviceId,
                        stateId = entity.id,
                        recordedAtMillis = evt.timestamp
                    )
                )
                return
            }

            // 2) Fallback: accept plain JSON
            val json = JSONObject(raw)
            val entity = DeviceStateEntity(
                id = ids.uuid(),
                deviceId = deviceId,
                isOnline = json.optBoolean("online", true),
                isOn = if (json.has("on")) json.optBoolean("on") else null,
                brightness = if (json.has("brightness")) json.optInt("brightness") else null,
                temperatureC = if (json.has("temp")) json.optDouble("temp") else null,
                humidityPercent = if (json.has("humidity")) json.optDouble("humidity") else null,
                batteryPercent = if (json.has("battery")) json.optInt("battery") else null,
                updatedAtMillis = now
            )
            deviceStateDao.upsert(entity)

            // ✅ ثبت History (Fallback)
            deviceHistoryDao.insert(
                DeviceHistoryEntity(
                    id = ids.uuid(),
                    deviceId = deviceId,
                    stateId = entity.id,
                    recordedAtMillis = now
                )
            )
        } catch (e: Exception) {
            logger.e("Parse error for $deviceId", e)
        }
    }

    private fun parseInt(map: Map<String, String>, key: String): Int? =
        map[key]?.trim()?.toIntOrNull()

    private fun parseDouble(map: Map<String, String>, key: String): Double? =
        map[key]?.trim()?.toDoubleOrNull()

    private fun parseBool(map: Map<String, String>, key: String): Boolean? {
        val v = map[key]?.trim()?.lowercase() ?: return null
        return when (v) {
            "true", "1", "yes", "on" -> true
            "false", "0", "no", "off" -> false
            else -> null
        }
    }
}
