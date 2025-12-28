package com.msa.msahub.features.devices.domain.model

data class DeviceTelemetry(
    val battery: Int? = null,
    val temperature: Double? = null,
    val humidity: Int? = null,
    val brightness: Int? = null,
    val power: String? = null,
    val locked: Boolean? = null,
    val motion: Boolean? = null,
    val raw: String? = null
)
