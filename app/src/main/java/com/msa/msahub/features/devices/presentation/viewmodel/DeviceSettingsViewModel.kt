package com.msa.msahub.features.devices.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.features.devices.presentation.state.DeviceSettingsUiEffect
import com.msa.msahub.features.devices.presentation.state.DeviceSettingsUiEvent
import com.msa.msahub.features.devices.presentation.state.DeviceSettingsUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceSettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(DeviceSettingsUiState())
    val state: StateFlow<DeviceSettingsUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DeviceSettingsUiEffect>(extraBufferCapacity = 16)
    val effects: SharedFlow<DeviceSettingsUiEffect> = _effects.asSharedFlow()

    fun onEvent(event: DeviceSettingsUiEvent) {
        when (event) {
            is DeviceSettingsUiEvent.SetDeviceId -> _state.value = _state.value.copy(deviceId = event.deviceId)
            DeviceSettingsUiEvent.Load -> load()
            is DeviceSettingsUiEvent.ToggleFavorite -> saveFavorite(event.value)
        }
    }

    private fun load() {
        // skeleton: no-op
    }

    private fun saveFavorite(value: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isFavorite = value)
            _effects.tryEmit(DeviceSettingsUiEffect.Saved)
        }
    }
}
