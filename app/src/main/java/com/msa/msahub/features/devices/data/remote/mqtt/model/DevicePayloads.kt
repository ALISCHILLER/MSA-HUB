package com.msa.msahub.features.devices.data.remote.mqtt.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceCommandPayload(
    val commandId: String,
    val action: String,
    val parameters: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis(),
    val version: String = "1.0"
)

@Serializable
data class DeviceStatePayload(
    val deviceId: String,
    val status: String,
    val values: Map<String, String>,
    val timestamp: Long
)
