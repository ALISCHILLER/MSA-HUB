package com.msa.msahub.features.devices.presentation.state

import com.msa.msahub.features.devices.domain.model.DeviceState

data class DeviceHistoryUiState(
    val deviceId: String = "",
    val isLoading: Boolean = false,
    val items: List<DeviceState> = emptyList(),
    val errorMessage: String? = null
)

sealed interface DeviceHistoryUiEvent {
    data class SetDeviceId(val deviceId: String) : DeviceHistoryUiEvent
    data object Load : DeviceHistoryUiEvent
    data object Refresh : DeviceHistoryUiEvent
}

sealed interface DeviceHistoryUiEffect {
    data class ShowError(val message: String) : DeviceHistoryUiEffect
}
