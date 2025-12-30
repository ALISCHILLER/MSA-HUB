package com.msa.msahub.features.devices.domain.model

data class DeviceCommand(
    val deviceId: String,
    val commandId: String,
    val action: String,
    val payloadJson: String = "{}"
)
