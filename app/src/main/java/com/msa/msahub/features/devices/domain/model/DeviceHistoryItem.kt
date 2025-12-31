package com.msa.msahub.features.devices.domain.model

data class DeviceHistoryItem(
    val id: String,
    val deviceId: String,
    val recordedAtMillis: Long
)
