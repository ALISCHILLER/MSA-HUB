package com.msa.msahub.core.platform.settings

data class MqttSettings(
    val host: String = "broker.example.com",
    val port: Int = 8883,
    val clientId: String = "msa_hub_android",
    val username: String? = null,
    val password: String? = null,
    val useTls: Boolean = true,
    val keepAliveSeconds: Int = 30,
    val enablePinning: Boolean = true,
    val pinnedCertAssetName: String = "mqtt_broker_ca"
)
