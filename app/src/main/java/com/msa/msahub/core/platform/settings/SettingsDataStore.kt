package com.msa.msahub.core.platform.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "msa_settings")

class SettingsDataStore(private val context: Context) {

    val mqttSettingsFlow: Flow<MqttSettings> = context.dataStore.data.map { prefs ->
        MqttSettings(
            host = prefs[KEY_MQTT_HOST] ?: "broker.msahub.com",
            port = prefs[KEY_MQTT_PORT] ?: 1883,
            clientId = prefs[KEY_MQTT_CLIENT_ID] ?: "msa_hub_android",
            username = prefs[KEY_MQTT_USERNAME],
            password = null, 
            useTls = prefs[KEY_MQTT_TLS] ?: false,
            keepAliveSeconds = prefs[KEY_MQTT_KEEPALIVE] ?: 60,
            enablePinning = prefs[KEY_MQTT_PINNING] ?: false,
            pinnedCertAssetName = prefs[KEY_MQTT_PINNED_CERT] ?: "mqtt_broker_ca"
        )
    }

    val apiBaseUrlFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_API_BASE_URL] ?: "https://api.msahub.com"
    }

    suspend fun setApiBaseUrl(url: String) {
        context.dataStore.edit { it[KEY_API_BASE_URL] = url.trim() }
    }

    suspend fun setMqttHost(host: String) { context.dataStore.edit { it[KEY_MQTT_HOST] = host.trim() } }
    suspend fun setMqttPort(port: Int) { context.dataStore.edit { it[KEY_MQTT_PORT] = port } }
    suspend fun setMqttClientId(clientId: String) { context.dataStore.edit { it[KEY_MQTT_CLIENT_ID] = clientId.trim() } }
    suspend fun setMqttUsername(username: String?) {
        context.dataStore.edit {
            if (username.isNullOrBlank()) it.remove(KEY_MQTT_USERNAME) else it[KEY_MQTT_USERNAME] = username.trim()
        }
    }
    suspend fun setMqttUseTls(use: Boolean) { context.dataStore.edit { it[KEY_MQTT_TLS] = use } }
    suspend fun setMqttKeepAlive(seconds: Int) { context.dataStore.edit { it[KEY_MQTT_KEEPALIVE] = seconds } }

    companion object {
        private val KEY_API_BASE_URL = stringPreferencesKey("api_base_url")
        private val KEY_MQTT_HOST = stringPreferencesKey("mqtt_host")
        private val KEY_MQTT_PORT = intPreferencesKey("mqtt_port")
        private val KEY_MQTT_CLIENT_ID = stringPreferencesKey("mqtt_client_id")
        private val KEY_MQTT_USERNAME = stringPreferencesKey("mqtt_username")
        private val KEY_MQTT_TLS = booleanPreferencesKey("mqtt_tls")
        private val KEY_MQTT_KEEPALIVE = intPreferencesKey("mqtt_keepalive")
        private val KEY_MQTT_PINNING = booleanPreferencesKey("mqtt_pinning")
        private val KEY_MQTT_PINNED_CERT = stringPreferencesKey("mqtt_pinned_cert")
    }
}
