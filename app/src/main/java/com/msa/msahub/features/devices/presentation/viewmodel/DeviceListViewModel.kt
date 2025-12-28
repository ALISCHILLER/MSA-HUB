package com.msa.msahub.features.devices.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.usecase.GetDevicesUseCase
import com.msa.msahub.features.devices.presentation.state.DeviceListUiEffect
import com.msa.msahub.features.devices.presentation.state.DeviceListUiEvent
import com.msa.msahub.features.devices.presentation.state.DeviceListUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceListViewModel(
    private val getDevices: GetDevicesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DeviceListUiState())
    val state: StateFlow<DeviceListUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DeviceListUiEffect>(extraBufferCapacity = 16)
    val effects: SharedFlow<DeviceListUiEffect> = _effects.asSharedFlow()

    fun onEvent(event: DeviceListUiEvent) {
        when (event) {
            DeviceListUiEvent.Load -> load(forceRefresh = false)
            DeviceListUiEvent.Refresh -> load(forceRefresh = true)
            is DeviceListUiEvent.ClickDevice -> _effects.tryEmit(DeviceListUiEffect.NavigateToDetail(event.deviceId))
        }
    }

    private fun load(forceRefresh: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            when (val result = getDevices(forceRefresh)) {
                is Result.Success -> _state.value = DeviceListUiState(isLoading = false, items = result.data)
                is Result.Failure -> {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = result.error.message)
                    _effects.tryEmit(DeviceListUiEffect.ShowError(result.error.message))
                }
            }
        }
    }
}
