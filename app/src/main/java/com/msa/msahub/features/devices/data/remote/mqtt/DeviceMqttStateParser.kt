package com.msa.msahub.features.devices.data.remote.mqtt

import com.msa.msahub.features.devices.domain.model.DeviceState
import org.json.JSONObject

/**
 * Expected payload JSON example:
 * {
 *   "deviceId": "dev-1",
 *   "isOnline": true,
 *   "isOn": true,
 *   "brightness": 80,
 *   "temperatureC": 23.5,
 *   "humidityPercent": 41.2,
 *   "batteryPercent": 95,
 *   "updatedAt": 1730000000000
 * }
 */
class DeviceMqttStateParser {

    fun parse(payload: ByteArray): DeviceState? {
        return runCatching {
            val json = JSONObject(payload.toString(Charsets.UTF_8))

            val deviceId = json.optString("deviceId")
            if (deviceId.isBlank()) return@runCatching null

            val isOnline = json.optBoolean("isOnline", true)

            DeviceState(
                deviceId = deviceId,
                isOnline = isOnline,
                isOn = json.optNullableBoolean("isOn"),
                brightness = json.optNullableInt("brightness"),
                temperatureC = json.optNullableDouble("temperatureC"),
                humidityPercent = json.optNullableDouble("humidityPercent"),
                batteryPercent = json.optNullableInt("batteryPercent"),
                updatedAtMillis = json.optLong("updatedAt", System.currentTimeMillis())
            )
        }.getOrNull()
    }

    private fun JSONObject.optNullableInt(key: String): Int? =
        if (has(key) && !isNull(key)) optInt(key) else null

    private fun JSONObject.optNullableDouble(key: String): Double? =
        if (has(key) && !isNull(key)) optDouble(key) else null

    private fun JSONObject.optNullableBoolean(key: String): Boolean? =
        if (has(key) && !isNull(key)) optBoolean(key) else null
}
