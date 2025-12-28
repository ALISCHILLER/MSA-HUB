package com.msa.msahub.features.devices.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.model.DeviceCommand
import com.msa.msahub.features.devices.domain.usecase.GetDeviceDetailUseCase
import com.msa.msahub.features.devices.domain.usecase.SendDeviceCommandUseCase
import com.msa.msahub.features.devices.domain.usecase.ObserveDeviceStateUseCase
import com.msa.msahub.features.devices.presentation.state.DeviceDetailUiEffect
import com.msa.msahub.features.devices.presentation.state.DeviceDetailUiEvent
import com.msa.msahub.features.devices.presentation.state.DeviceDetailUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeviceDetailViewModel(
    private val getDetail: GetDeviceDetailUseCase,
    private val sendCommand: SendDeviceCommandUseCase,
    private val observeState: ObserveDeviceStateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DeviceDetailUiState())
    val state: StateFlow<DeviceDetailUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DeviceDetailUiEffect>(extraBufferCapacity = 16)
    val effects: SharedFlow<DeviceDetailUiEffect> = _effects.asSharedFlow()

    fun onEvent(event: DeviceDetailUiEvent) {
        when (event) {
            is DeviceDetailUiEvent.SetDeviceId -> {
                _state.value = _state.value.copy(deviceId = event.deviceId)
                startObservingState(event.deviceId)
            }
            DeviceDetailUiEvent.Load -> load(forceRefresh = false)
            DeviceDetailUiEvent.Refresh -> load(forceRefresh = true)

            is DeviceDetailUiEvent.SendCommand -> send(action = event.action)

            DeviceDetailUiEvent.OpenHistory -> _effects.tryEmit(DeviceDetailUiEffect.NavigateToHistory(_state.value.deviceId))
            DeviceDetailUiEvent.OpenSettings -> _effects.tryEmit(DeviceDetailUiEffect.NavigateToSettings(_state.value.deviceId))
            DeviceDetailUiEvent.Retry -> load(forceRefresh = true)
        }
    }

    private fun load(forceRefresh: Boolean) {
        val id = _state.value.deviceId
        if (id.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            when (val result = getDetail(id, forceRefresh)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, device = result.data)
                is Result.Failure -> {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = result.error.message)
                    _effects.tryEmit(DeviceDetailUiEffect.ShowError(result.error.message))
                }
            }
        }
    }

    private fun startObservingState(deviceId: String) {
        viewModelScope.launch {
            observeState(deviceId).collectLatest { s ->
                _state.value = _state.value.copy(state = s)
            }
        }
    }

    private fun send(action: String) {
        val id = _state.value.deviceId
        if (id.isBlank()) return

        viewModelScope.launch {
            val cmd = DeviceCommand(
                deviceId = id,
                action = action,
                params = emptyMap(),
                createdAtMillis = System.currentTimeMillis()
            )

            when (val result = sendCommand(cmd)) {
                is Result.Success -> _effects.tryEmit(DeviceDetailUiEffect.CommandResult(result.data))
                is Result.Failure -> _effects.tryEmit(DeviceDetailUiEffect.ShowError(result.error.message))
            }
        }
    }
}
