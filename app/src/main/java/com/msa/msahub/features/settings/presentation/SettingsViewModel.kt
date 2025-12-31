package com.msa.msahub.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.security.auth.AuthTokenStore
import com.msa.msahub.core.security.storage.SecurePrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val mqttHost: String = "",
    val mqttPort: String = "",
    val apiBaseUrl: String = "",
    val authToken: String = "",
    val isSaved: Boolean = false
)

class SettingsViewModel(
    private val securePrefs: SecurePrefs,
    private val authTokenStore: AuthTokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _state.update { it.copy(
                mqttHost = securePrefs.getString("mqtt_host") ?: "broker.msahub.com",
                mqttPort = securePrefs.getString("mqtt_port") ?: "1883",
                apiBaseUrl = securePrefs.getString("api_url") ?: "https://api.msahub.com",
                authToken = authTokenStore.getToken() ?: ""
            ) }
        }
    }

    fun onMqttHostChange(value: String) = _state.update { it.copy(mqttHost = value) }
    fun onMqttPortChange(value: String) = _state.update { it.copy(mqttPort = value) }
    fun onApiUrlChange(value: String) = _state.update { it.copy(apiBaseUrl = value) }
    fun onTokenChange(value: String) = _state.update { it.copy(authToken = value) }

    fun saveSettings() {
        viewModelScope.launch {
            securePrefs.putString("mqtt_host", _state.value.mqttHost)
            securePrefs.putString("mqtt_port", _state.value.mqttPort)
            securePrefs.putString("api_url", _state.value.apiBaseUrl)
            authTokenStore.saveToken(_state.value.authToken)
            _state.update { it.copy(isSaved = true) }
        }
    }
}
