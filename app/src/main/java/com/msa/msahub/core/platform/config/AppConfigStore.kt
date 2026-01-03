package com.msa.msahub.core.platform.config

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.msa.msahub.app.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "msa_config")

class AppConfigStore(private val context: Context) {

    private object Keys {
        val ENV = stringPreferencesKey("env")
        val API_BASE_URL = stringPreferencesKey("api_base_url")
        val CONNECT_TIMEOUT = longPreferencesKey("connect_timeout_ms")
        val REQUEST_TIMEOUT = longPreferencesKey("request_timeout_ms")

        val MQTT_HOST = stringPreferencesKey("mqtt_host")
        val MQTT_PORT = intPreferencesKey("mqtt_port")
        val MQTT_TLS = booleanPreferencesKey("mqtt_tls")
    }

    fun observe(): Flow<RuntimeConfig> = context.dataStore.data.map { p ->
        val env = p[Keys.ENV]?.let { runCatching { Environment.valueOf(it) }.getOrNull() } ?: Environment.DEV

        val http = HttpRuntimeConfig(
            baseUrl = p[Keys.API_BASE_URL] ?: if (env == Environment.PROD) AppConfig.API_BASE_URL_PROD else AppConfig.API_BASE_URL_DEV,
            connectTimeoutMs = p[Keys.CONNECT_TIMEOUT] ?: 30_000L,
            requestTimeoutMs = p[Keys.REQUEST_TIMEOUT] ?: 30_000L
        )

        val mqtt = MqttRuntimeConfig(
            host = p[Keys.MQTT_HOST] ?: if (env == Environment.PROD) AppConfig.MQTT_HOST_PROD else AppConfig.MQTT_HOST_DEV,
            port = p[Keys.MQTT_PORT] ?: if (env == Environment.PROD) AppConfig.MQTT_PORT_TLS else 1883,
            useTls = p[Keys.MQTT_TLS] ?: (env == Environment.PROD),
            clientIdPrefix = "msahub_android",
            username = null, // Secrets are handled by SecretsStore
            password = null  // Secrets are handled by SecretsStore
        )

        RuntimeConfig(
            env = env,
            http = http,
            mqtt = mqtt,
            sync = SyncPolicy()
        )
    }

    suspend fun setEnv(env: Environment) {
        context.dataStore.edit { it[Keys.ENV] = env.name }
    }

    suspend fun setApiBaseUrl(url: String) {
        context.dataStore.edit { it[Keys.API_BASE_URL] = url }
    }

    suspend fun setHttpTimeouts(connectTimeoutMs: Long, requestTimeoutMs: Long) {
        context.dataStore.edit {
            it[Keys.CONNECT_TIMEOUT] = connectTimeoutMs
            it[Keys.REQUEST_TIMEOUT] = requestTimeoutMs
        }
    }

    suspend fun setMqttHost(host: String) {
        context.dataStore.edit { it[Keys.MQTT_HOST] = host }
    }

    suspend fun setMqttPort(port: Int) {
        context.dataStore.edit { it[Keys.MQTT_PORT] = port }
    }

    suspend fun setMqttUseTls(useTls: Boolean) {
        context.dataStore.edit { it[Keys.MQTT_TLS] = useTls }
    }

    @Deprecated("Secrets are stored in SecretsStore (SecurePrefs). Use ConfigRepository.setMqttUsername()")
    suspend fun setMqttUser(user: String?) { /* no-op */ }

    @Deprecated("Secrets are stored in SecretsStore (SecurePrefs). Use ConfigRepository.setMqttPassword()")
    suspend fun setMqttPass(pass: String?) { /* no-op */ }

    companion object {
        fun defaultRuntimeConfig(): RuntimeConfig {
            return RuntimeConfig(
                env = Environment.DEV,
                http = HttpRuntimeConfig(
                    baseUrl = AppConfig.API_BASE_URL_DEV,
                    connectTimeoutMs = 30_000,
                    requestTimeoutMs = 30_000
                ),
                mqtt = MqttRuntimeConfig(
                    host = AppConfig.MQTT_HOST_DEV,
                    port = 1883,
                    useTls = false,
                    clientIdPrefix = "msahub_android",
                    username = null,
                    password = null,
                    keepAliveSec = 60
                ),
                sync = SyncPolicy()
            )
        }
    }
}
