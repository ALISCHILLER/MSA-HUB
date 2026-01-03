package com.msa.msahub.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.platform.config.AppConfigStore
import com.msa.msahub.core.security.auth.AuthTokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val mqttHost: String = "",
    val mqttPort: String = "",
    val mqttUseTls: Boolean = false,
    val mqttUsername: String = "",
    val mqttPassword: String = "",
    val apiBaseUrl: String = "",
    val authToken: String = "",
    val isSaved: Boolean = false
)

class SettingsViewModel(
    private val configStore: AppConfigStore,
    private val authTokenStore: AuthTokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            configStore.observe().collect { cfg ->
                _state.update {
                    it.copy(
                        mqttHost = cfg.mqtt.host,
                        mqttPort = cfg.mqtt.port.toString(),
                        mqttUseTls = cfg.mqtt.useTls,
                        mqttUsername = cfg.mqtt.username.orEmpty(),
                        mqttPassword = cfg.mqtt.password.orEmpty(),
                        apiBaseUrl = cfg.http.baseUrl,
                        authToken = authTokenStore.getToken() ?: "",
                        isSaved = false
                    )
                }
            }
        }
    }

    fun onMqttHostChange(value: String) = _state.update { it.copy(mqttHost = value) }
    fun onMqttPortChange(value: String) = _state.update { it.copy(mqttPort = value) }
    fun onMqttTlsChange(value: Boolean) = _state.update { it.copy(mqttUseTls = value) }
    fun onMqttUsernameChange(value: String) = _state.update { it.copy(mqttUsername = value) }
    fun onMqttPasswordChange(value: String) = _state.update { it.copy(mqttPassword = value) }
    fun onApiUrlChange(value: String) = _state.update { it.copy(apiBaseUrl = value) }
    fun onTokenChange(value: String) = _state.update { it.copy(authToken = value) }

    fun saveSettings() {
        viewModelScope.launch {
            configStore.setMqttHost(_state.value.mqttHost)
            configStore.setMqttPort(_state.value.mqttPort.toIntOrNull() ?: 1883)
            configStore.setMqttUseTls(_state.value.mqttUseTls)
            configStore.setMqttUser(_state.value.mqttUsername.ifBlank { null })
            configStore.setMqttPass(_state.value.mqttPassword.ifBlank { null })
            configStore.setApiBaseUrl(_state.value.apiBaseUrl)
            authTokenStore.saveToken(_state.value.authToken)
            _state.update { it.copy(isSaved = true) }
        }
    }
}
