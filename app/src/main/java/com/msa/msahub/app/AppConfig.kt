package com.msa.msahub.app

object AppConfig {
    const val DEFAULT_TIMEOUT_MS = 30_000L

    const val API_BASE_URL_DEV = "https://dev.api.msahub.local/"
    const val API_BASE_URL_PROD = "https://api.msa-hub.com/v1/"

    const val MQTT_HOST_DEV = "dev.mqtt.msahub.local"
    const val MQTT_HOST_PROD = "mqtt.msa-hub.com"
    const val MQTT_PORT_TLS = 8883
}
