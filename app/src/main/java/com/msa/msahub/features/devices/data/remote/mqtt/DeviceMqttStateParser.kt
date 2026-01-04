package com.msa.msahub.features.devices.data.remote.mqtt

import com.msa.msahub.features.devices.domain.model.DeviceState
import org.json.JSONObject

class DeviceMqttStateParser {

    fun parse(payload: ByteArray): DeviceState? {
        return runCatching {
            val json = JSONObject(payload.toString(Charsets.UTF_8))
            val deviceId = json.optString("deviceId")
            if (deviceId.isBlank()) return@runCatching null

            DeviceState(
                deviceId = deviceId,
                isOnline = json.optBoolean("isOnline", true),
                isOn = json.optNullableBoolean("isOn"),
                brightness = json.optNullableInt("brightness"),
                temperatureC = json.optNullableDouble("temperatureC"),
                humidityPercent = json.optNullableDouble("humidityPercent"),
                batteryPercent = json.optNullableInt("batteryPercent"),
                updatedAtMillis = json.optLong("updatedAt", System.currentTimeMillis())
            )
        }.getOrNull()
    }

    fun parseFromStateMap(
        deviceId: String,
        online: Boolean,
        state: Map<String, Any?>,
        timestamp: Long
    ): DeviceState {
        val json = JSONObject(state)
        return DeviceState(
            deviceId = deviceId,
            isOnline = online,
            isOn = json.optNullableBoolean("isOn"),
            brightness = json.optNullableInt("brightness"),
            temperatureC = json.optNullableDouble("temperatureC"),
            humidityPercent = json.optNullableDouble("humidityPercent"),
            batteryPercent = json.optNullableInt("batteryPercent"),
            updatedAtMillis = timestamp
        )
    }

    private fun JSONObject.optNullableInt(key: String): Int? =
        if (has(key) && !isNull(key)) optInt(key) else null

    private fun JSONObject.optNullableDouble(key: String): Double? =
        if (has(key) && !isNull(key)) optDouble(key) else null

    private fun JSONObject.optNullableBoolean(key: String): Boolean? =
        if (has(key) && !isNull(key)) optBoolean(key) else null
}
