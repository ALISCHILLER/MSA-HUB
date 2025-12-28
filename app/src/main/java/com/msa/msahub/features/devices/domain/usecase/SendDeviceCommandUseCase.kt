package com.msa.msahub.features.devices.domain.usecase

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.model.CommandAck
import com.msa.msahub.features.devices.domain.model.DeviceCommand
import com.msa.msahub.features.devices.domain.repository.DeviceRepository

class SendDeviceCommandUseCase(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(command: DeviceCommand): Result<CommandAck> {
        return repository.sendCommand(command)
    }
}
