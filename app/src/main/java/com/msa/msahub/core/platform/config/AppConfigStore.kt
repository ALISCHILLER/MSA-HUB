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
        val MQTT_HOST = stringPreferencesKey("mqtt_host")
        val MQTT_PORT = intPreferencesKey("mqtt_port")
        val MQTT_TLS = booleanPreferencesKey("mqtt_tls")
        val MQTT_USER = stringPreferencesKey("mqtt_user")
        val MQTT_PASS = stringPreferencesKey("mqtt_pass")
    }

    fun observe(): Flow<RuntimeConfig> = context.dataStore.data.map { p ->
        val env = p[Keys.ENV]?.let { runCatching { Environment.valueOf(it) }.getOrNull() } ?: Environment.DEV

        val mqtt = MqttRuntimeConfig(
            host = p[Keys.MQTT_HOST] ?: if (env == Environment.PROD) AppConfig.MQTT_HOST_PROD else AppConfig.MQTT_HOST_DEV,
            port = p[Keys.MQTT_PORT] ?: if (env == Environment.PROD) AppConfig.MQTT_PORT_TLS else 1883,
            useTls = p[Keys.MQTT_TLS] ?: (env == Environment.PROD),
            clientIdPrefix = "msahub_android",
            username = p[Keys.MQTT_USER],
            password = p[Keys.MQTT_PASS]
        )

        RuntimeConfig(
            env = env,
            mqtt = mqtt,
            sync = SyncPolicy()
        )
    }

    suspend fun setEnv(env: Environment) {
        context.dataStore.edit { it[Keys.ENV] = env.name }
    }
}
