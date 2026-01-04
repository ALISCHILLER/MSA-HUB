package com.msa.msahub.core.platform.config

enum class Environment { DEV, STAGING, PROD }

data class HttpRuntimeConfig(
    val baseUrl: String,
    val connectTimeoutMs: Long = 30_000,
    val requestTimeoutMs: Long = 30_000
)

data class MqttRuntimeConfig(
    val host: String,
    val port: Int,
    val useTls: Boolean,
    val clientIdPrefix: String,
    val username: String? = null,
    val password: String? = null,
    val keepAliveSec: Int = 60
)

data class SyncPolicy(
    val periodicMinutes: Long = 15,
    val outboxFlushMinutes: Long = 5,
    val healthCheckMinutes: Long = 15,
    val requireUnmetered: Boolean = false
)

data class RuntimeConfig(
    val env: Environment = Environment.DEV,
    val http: HttpRuntimeConfig,
    val mqtt: MqttRuntimeConfig,
    val sync: SyncPolicy
)
