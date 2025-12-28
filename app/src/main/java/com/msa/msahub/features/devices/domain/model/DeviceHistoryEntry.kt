package com.msa.msahub.features.devices.domain.model

data class DeviceHistoryEntry(
    val id: String,
    val deviceId: String,
    val eventType: String,
    val details: String,
    val timestamp: Long
)
