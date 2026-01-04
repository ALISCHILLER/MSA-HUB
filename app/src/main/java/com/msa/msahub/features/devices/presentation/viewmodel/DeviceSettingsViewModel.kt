package com.msa.msahub.features.devices.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.features.devices.data.local.dao.DeviceDao
import com.msa.msahub.features.devices.presentation.state.DeviceSettingsState
import com.msa.msahub.features.devices.presentation.state.DeviceSettingsUiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeviceSettingsViewModel(
    private val deviceDao: DeviceDao
) : ViewModel() {

    private val _state = MutableStateFlow(DeviceSettingsState())
    val state: StateFlow<DeviceSettingsState> = _state

    fun onEvent(event: DeviceSettingsUiEvent) {
        when (event) {
            is DeviceSettingsUiEvent.Load -> load(event.deviceId)
            is DeviceSettingsUiEvent.NameChanged -> onNameChange(event.name)
            is DeviceSettingsUiEvent.FavoriteChanged -> onFavoriteToggle(event.isFavorite)
            DeviceSettingsUiEvent.Save -> save()
        }
    }

    private fun load(deviceId: String) {
        if (_state.value.deviceId == deviceId && !_state.value.isLoading) return

        _state.update { it.copy(deviceId = deviceId, isLoading = true, error = null, saved = false) }

        viewModelScope.launch {
            deviceDao.observeById(deviceId).collect { entity ->
                if (entity == null) {
                    _state.update { it.copy(isLoading = false, error = "Device not found") }
                } else {
                    _state.update {
                        it.copy(
                            name = entity.name,
                            isFavorite = entity.isFavorite,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            }
        }
    }

    private fun onNameChange(value: String) {
        _state.update { it.copy(name = value, saved = false) }
    }

    private fun onFavoriteToggle(value: Boolean) {
        _state.update { it.copy(isFavorite = value, saved = false) }
    }

    private fun save() {
        val s = _state.value
        if (s.deviceId.isBlank()) return

        _state.update { it.copy(isSaving = true, error = null, saved = false) }

        viewModelScope.launch {
            runCatching {
                deviceDao.updateName(s.deviceId, s.name.trim())
                deviceDao.updateFavorite(s.deviceId, s.isFavorite)
            }.onSuccess {
                _state.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message ?: "Save failed") }
            }
        }
    }
}
