package com.msa.msahub.features.devices.domain.model

data class Device(
    val id: String,
    val name: String,
    val type: DeviceType = DeviceType.UNKNOWN,
    val capabilities: List<DeviceCapability> = emptyList(),
    val updatedAt: Long = 0L
)
