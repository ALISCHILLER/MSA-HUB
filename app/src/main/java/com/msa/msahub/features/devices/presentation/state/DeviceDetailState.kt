package com.msa.msahub.features.devices.presentation.state

import com.msa.msahub.features.devices.domain.model.CommandAck
import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.devices.domain.model.DeviceCommand
import com.msa.msahub.features.devices.domain.model.DeviceState

data class DeviceDetailUiState(
    val deviceId: String = "",
    val isLoading: Boolean = false,
    val device: Device? = null,
    val state: DeviceState? = null,
    val errorMessage: String? = null
)

sealed interface DeviceDetailUiEvent {
    data class SetDeviceId(val deviceId: String) : DeviceDetailUiEvent
    data object Load : DeviceDetailUiEvent
    data object Refresh : DeviceDetailUiEvent
    data class SendCommand(val action: String) : DeviceDetailUiEvent
    data object OpenHistory : DeviceDetailUiEvent
    data object OpenSettings : DeviceDetailUiEvent
    data object Retry : DeviceDetailUiEvent
}

sealed interface DeviceDetailUiEffect {
    data class ShowError(val message: String) : DeviceDetailUiEffect
    data class NavigateToHistory(val deviceId: String) : DeviceDetailUiEffect
    data class NavigateToSettings(val deviceId: String) : DeviceDetailUiEffect
    data class CommandResult(val ack: CommandAck) : DeviceDetailUiEffect
}
