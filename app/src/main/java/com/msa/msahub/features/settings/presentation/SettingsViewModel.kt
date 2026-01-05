package com.msa.msahub.features.settings.presentation

import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.platform.config.AppConfigStore
import com.msa.msahub.core.platform.network.LocalDiscoveryManager
import com.msa.msahub.core.security.auth.AuthTokenStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val mqttHost: String = "",
    val mqttPort: String = "",
    val mqttUseTls: Boolean = false,
    val mqttUsername: String = "",
    val mqttPassword: String = "",
    val apiBaseUrl: String = "",
    val authToken: String = "",
    val isSaved: Boolean = false,
    val isScanningLocal: Boolean = false,
    val discoveredHubs: List<NsdServiceInfo> = emptyList()
)

class SettingsViewModel(
    private val configStore: AppConfigStore,
    private val authTokenStore: AuthTokenStore,
    private val localDiscoveryManager: LocalDiscoveryManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        observeSettings()
        observeLocalServices()
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
                        authToken = authTokenStore.getToken() ?: ""
                    )
                }
            }
        }
    }

    private fun observeLocalServices() {
        viewModelScope.launch {
            localDiscoveryManager.discoveredServices.collect { services ->
                _state.update { it.copy(discoveredHubs = services) }
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

    fun startLocalDiscovery() {
        _state.update { it.copy(isScanningLocal = true) }
        localDiscoveryManager.startDiscovery()
    }

    fun stopLocalDiscovery() {
        _state.update { it.copy(isScanningLocal = false) }
        localDiscoveryManager.stopDiscovery()
    }

    fun selectDiscoveredHub(hub: NsdServiceInfo) {
        _state.update { 
            it.copy(
                mqttHost = hub.host.hostAddress ?: "",
                mqttPort = hub.port.toString()
            )
        }
        stopLocalDiscovery()
    }

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
