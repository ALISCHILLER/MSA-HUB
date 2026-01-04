package com.msa.msahub.features.devices.presentation.state

data class DeviceSettingsState(
    val deviceId: String = "",
    val name: String = "",
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

sealed interface DeviceSettingsUiEvent {
    data class Load(val deviceId: String) : DeviceSettingsUiEvent
    data class NameChanged(val name: String) : DeviceSettingsUiEvent
    data class FavoriteChanged(val isFavorite: Boolean) : DeviceSettingsUiEvent
    data object Save : DeviceSettingsUiEvent
}

sealed interface DeviceSettingsUiEffect {
    data class ShowError(val message: String) : DeviceSettingsUiEffect
    data object Saved : DeviceSettingsUiEffect
}
