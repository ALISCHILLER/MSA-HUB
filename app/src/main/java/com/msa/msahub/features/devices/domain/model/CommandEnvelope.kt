package com.msa.msahub.features.devices.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CommandEnvelope(
    @SerialName("commandId") val commandId: String,
    @SerialName("deviceId") val deviceId: String,
    @SerialName("action") val action: String,
    @SerialName("payload") val payload: JsonElement? = null,
    @SerialName("ts") val timestamp: Long
)
