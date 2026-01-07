package com.msa.msahub.core.platform.config

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.msa.msahub.app.AppConfig
import com.msa.msahub.core.security.storage.SecurePrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

private val Context.dataStore by preferencesDataStore(name = "msa_config")

class AppConfigStore(
    private val context: Context,
    private val securePrefs: SecurePrefs
) {

    private object Keys {
        val ENV = stringPreferencesKey("env")
        val API_BASE_URL = stringPreferencesKey("api_base_url")
        val CONNECT_TIMEOUT = longPreferencesKey("connect_timeout_ms")
        val REQUEST_TIMEOUT = longPreferencesKey("request_timeout_ms")

        val MQTT_HOST = stringPreferencesKey("mqtt_host")
        val MQTT_PORT = intPreferencesKey("mqtt_port")
        val MQTT_TLS = booleanPreferencesKey("mqtt_tls")
        val MQTT_CLIENT_ID_PREFIX = stringPreferencesKey("mqtt_client_id_prefix")
    }

    fun observe(): Flow<RuntimeConfig> = context.dataStore.data.map { p ->
        val env = p[Keys.ENV]?.let { runCatching { Environment.valueOf(it) }.getOrNull() } ?: Environment.DEV

        val http = HttpRuntimeConfig(
            baseUrl = p[Keys.API_BASE_URL] ?: if (env == Environment.PROD) AppConfig.API_BASE_URL_PROD else AppConfig.API_BASE_URL_DEV,
            connectTimeoutMs = p[Keys.CONNECT_TIMEOUT] ?: 30_000L,
            requestTimeoutMs = p[Keys.REQUEST_TIMEOUT] ?: 30_000L
        )

        // تهیه یا تولید ClientId یکتا به صورت امن
        val uniqueId = getOrCreateUniqueDeviceId()

        val mqtt = MqttRuntimeConfig(
            host = p[Keys.MQTT_HOST] ?: if (env == Environment.PROD) AppConfig.MQTT_HOST_PROD else AppConfig.MQTT_HOST_DEV,
            port = p[Keys.MQTT_PORT] ?: if (env == Environment.PROD) AppConfig.MQTT_PORT_TLS else 1883,
            useTls = p[Keys.MQTT_TLS] ?: (env == Environment.PROD),
            clientIdPrefix = (p[Keys.MQTT_CLIENT_ID_PREFIX] ?: "msahub_android") + "-" + uniqueId,
            username = securePrefs.getString("mqtt_user"),
            password = securePrefs.getString("mqtt_pass"),
            keepAliveSec = 60
        )

        RuntimeConfig(
            env = env,
            http = http,
            mqtt = mqtt,
            sync = SyncPolicy()
        )
    }

    private fun getOrCreateUniqueDeviceId(): String {
        var id = securePrefs.getString("internal_device_uuid")
        if (id.isNullOrBlank()) {
            id = UUID.randomUUID().toString().take(8) // کوتاه برای خوانایی، اما کافی برای یکتا بودن
            securePrefs.saveString("internal_device_uuid", id)
        }
        return id
    }

    suspend fun setMqttUsername(user: String?) {
        securePrefs.saveString("mqtt_user", user ?: "")
    }

    suspend fun setMqttPassword(pass: String?) {
        securePrefs.saveString("mqtt_pass", pass ?: "")
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

    suspend fun setApiBaseUrl(url: String) {
        context.dataStore.edit { it[Keys.API_BASE_URL] = url }
    }

    companion object {
        fun defaultRuntimeConfig(): RuntimeConfig {
            return RuntimeConfig(
                env = Environment.DEV,
                http = HttpRuntimeConfig(baseUrl = AppConfig.API_BASE_URL_DEV),
                mqtt = MqttRuntimeConfig(
                    host = AppConfig.MQTT_HOST_DEV,
                    port = 1883,
                    useTls = false,
                    clientIdPrefix = "msahub_android"
                ),
                sync = SyncPolicy()
            )
        }
    }
}
