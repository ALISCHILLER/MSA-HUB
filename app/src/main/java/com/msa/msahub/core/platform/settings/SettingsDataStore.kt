package com.msa.msahub.core.platform.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow

private val Context.dataStore by preferencesDataStore(name = "msa_settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val HOST = stringPreferencesKey("mqtt_host")
        val PORT = intPreferencesKey("mqtt_port")
        val CLIENT_ID = stringPreferencesKey("mqtt_client_id")
        val USERNAME = stringPreferencesKey("mqtt_username")
        val PASSWORD = stringPreferencesKey("mqtt_password")
        val USE_TLS = booleanPreferencesKey("mqtt_use_tls")
        val KEEP_ALIVE = intPreferencesKey("mqtt_keep_alive")
        val ENABLE_PINNING = booleanPreferencesKey("mqtt_enable_pinning")
        val PINNED_CERT = stringPreferencesKey("mqtt_pinned_cert")
    }

    val flow: Flow<Preferences> = context.dataStore.data

    suspend fun setHost(v: String) = context.dataStore.edit { it[Keys.HOST] = v }
    suspend fun setPort(v: Int) = context.dataStore.edit { it[Keys.PORT] = v }
    suspend fun setClientId(v: String) = context.dataStore.edit { it[Keys.CLIENT_ID] = v }

    suspend fun setUsername(v: String?) = context.dataStore.edit {
        if (v.isNullOrBlank()) it.remove(Keys.USERNAME) else it[Keys.USERNAME] = v
    }
    suspend fun setPassword(v: String?) = context.dataStore.edit {
        if (v.isNullOrBlank()) it.remove(Keys.PASSWORD) else it[Keys.PASSWORD] = v
    }

    suspend fun setUseTls(v: Boolean) = context.dataStore.edit { it[Keys.USE_TLS] = v }
    suspend fun setKeepAlive(v: Int) = context.dataStore.edit { it[Keys.KEEP_ALIVE] = v }

    suspend fun setEnablePinning(v: Boolean) = context.dataStore.edit { it[Keys.ENABLE_PINNING] = v }
    suspend fun setPinnedCert(v: String) = context.dataStore.edit { it[Keys.PINNED_CERT] = v }

    fun mapToDomain(prefs: Preferences): MqttSettings {
        return MqttSettings(
            host = prefs[Keys.HOST] ?: "broker.example.com",
            port = prefs[Keys.PORT] ?: 8883,
            clientId = prefs[Keys.CLIENT_ID] ?: "msa_hub_android",
            username = prefs[Keys.USERNAME],
            password = prefs[Keys.PASSWORD],
            useTls = prefs[Keys.USE_TLS] ?: true,
            keepAliveSeconds = prefs[Keys.KEEP_ALIVE] ?: 30,
            enablePinning = prefs[Keys.ENABLE_PINNING] ?: true,
            pinnedCertAssetName = prefs[Keys.PINNED_CERT] ?: "mqtt_broker_ca"
        )
    }
}
