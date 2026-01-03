package com.msa.msahub.features.devices.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.usecase.GetDeviceHistoryUseCase
import com.msa.msahub.features.devices.presentation.state.DeviceHistoryUiEffect
import com.msa.msahub.features.devices.presentation.state.DeviceHistoryUiEvent
import com.msa.msahub.features.devices.presentation.state.DeviceHistoryUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceHistoryViewModel(
    private val getHistory: GetDeviceHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DeviceHistoryUiState())
    val state: StateFlow<DeviceHistoryUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DeviceHistoryUiEffect>(extraBufferCapacity = 16)
    val effects: SharedFlow<DeviceHistoryUiEffect> = _effects.asSharedFlow()

    fun onEvent(event: DeviceHistoryUiEvent) {
        when (event) {
            is DeviceHistoryUiEvent.SetDeviceId -> _state.value = _state.value.copy(deviceId = event.deviceId)
            DeviceHistoryUiEvent.Load -> load(force = false)
            DeviceHistoryUiEvent.Refresh -> load(force = true)
        }
    }

    private fun load(force: Boolean) {
        val id = _state.value.deviceId
        if (id.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            when (val result = getHistory(id, limit = 50)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, items = result.data)
                is Result.Failure -> {
                    val msg = result.error.message ?: "Unknown Error"
                    _state.value = _state.value.copy(isLoading = false, errorMessage = msg)
                    _effects.tryEmit(DeviceHistoryUiEffect.ShowError(msg))
                }
            }
        }
    }
}
