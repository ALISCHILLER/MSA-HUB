package com.msa.msahub.features.devices.presentation.state

data class DeviceSettingsUiState(
    val deviceId: String = "",
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)

sealed interface DeviceSettingsUiEvent {
    data class SetDeviceId(val deviceId: String) : DeviceSettingsUiEvent
    data object Load : DeviceSettingsUiEvent
    data class ToggleFavorite(val value: Boolean) : DeviceSettingsUiEvent
}

sealed interface DeviceSettingsUiEffect {
    data class ShowError(val message: String) : DeviceSettingsUiEffect
    data object Saved : DeviceSettingsUiEffect
}
