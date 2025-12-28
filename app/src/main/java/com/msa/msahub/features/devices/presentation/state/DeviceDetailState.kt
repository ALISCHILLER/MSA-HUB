package com.msa.msahub.features.devices.presentation.state

import com.msa.msahub.core.common.AppError
import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.devices.domain.model.DeviceCommand

data class DeviceDetailUiState(
    val isLoading: Boolean = false,
    val device: Device? = null,
    val error: AppError? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.OFFLINE,
)

sealed interface DeviceDetailUiEvent {
    data class LoadDevice(val deviceId: String) : DeviceDetailUiEvent
    data class SendCommand(val command: DeviceCommand) : DeviceDetailUiEvent
    data object Retry : DeviceDetailUiEvent
}

sealed interface DeviceDetailUiEffect {
    data class ShowToast(val message: String) : DeviceDetailUiEffect
    data class NavigateBack(val deviceId: String) : DeviceDetailUiEffect
}

enum class ConnectionStatus { CLOUD, WIFI_LOCAL, BLUETOOTH, OFFLINE }
