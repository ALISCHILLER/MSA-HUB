package com.msa.msahub.features.devices.presentation.state

import com.msa.msahub.features.devices.domain.model.Device

data class DeviceListUiState(
    val isLoading: Boolean = false,
    val items: List<Device> = emptyList(),
    val errorMessage: String? = null
)

sealed interface DeviceListUiEvent {
    data object Load : DeviceListUiEvent
    data object Refresh : DeviceListUiEvent
    data class ClickDevice(val deviceId: String) : DeviceListUiEvent
}

sealed interface DeviceListUiEffect {
    data class NavigateToDetail(val deviceId: String) : DeviceListUiEffect
    data class ShowError(val message: String) : DeviceListUiEffect
}
