package com.msa.msahub.features.devices.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceCommand(
    val id: String,
    val deviceId: String,
    val type: String,
    val payload: String,
    val timestamp: Long = System.currentTimeMillis()
)
