package com.msa.msahub.features.devices.domain.model

data class DeviceState(
    val deviceId: String,
    val isOnline: Boolean,
    val isOn: Boolean? = null,
    val brightness: Int? = null,         // 0..100
    val temperatureC: Double? = null,
    val humidityPercent: Double? = null,
    val batteryPercent: Int? = null,     // 0..100
    val updatedAtMillis: Long
)
