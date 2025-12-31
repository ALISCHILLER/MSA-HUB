package com.msa.msahub.features.devices.domain.model

import java.util.UUID

data class DeviceCommand(
    val deviceId: String,
    val action: String,
    val params: Map<String, Any?> = emptyMap(),
    val createdAtMillis: Long = System.currentTimeMillis(),
    val commandId: String = UUID.randomUUID().toString()
)
