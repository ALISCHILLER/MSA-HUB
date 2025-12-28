package com.msa.msahub.core.platform.network.mqtt

data class MqttConfig(
    val host: String,
    val port: Int,
    val clientId: String,
    val useTls: Boolean = true
)
