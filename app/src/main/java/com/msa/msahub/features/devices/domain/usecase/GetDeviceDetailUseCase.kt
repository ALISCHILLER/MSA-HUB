package com.msa.msahub.features.devices.domain.usecase

import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow

class GetDeviceDetailUseCase(
    private val repository: DeviceRepository
) {
    operator fun invoke(deviceId: String): Flow<com.msa.msahub.features.devices.domain.model.Device?> {
        return repository.observeDevice(deviceId)
    }
}
