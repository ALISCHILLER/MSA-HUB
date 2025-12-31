package com.msa.msahub.features.devices.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: String,
    val name: String,
    val type: DeviceType = DeviceType.UNKNOWN,
    val capabilities: Set<DeviceCapability> = emptySet(),
    val isFavorite: Boolean = false,
    val roomName: String? = null,
    val lastSeenMillis: Long = 0L
)
