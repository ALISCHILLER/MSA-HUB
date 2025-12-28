package com.msa.msahub.features.devices.domain.model

data class Device(
    val id: String,
    val name: String,
    val type: DeviceType,
    val capabilities: Set<DeviceCapability>,
    val isFavorite: Boolean,
    val roomName: String?,
    val lastSeenMillis: Long
)
