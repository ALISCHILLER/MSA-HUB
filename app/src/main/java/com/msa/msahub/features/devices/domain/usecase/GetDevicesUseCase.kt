package com.msa.msahub.features.devices.domain.usecase

import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow

class GetDevicesUseCase(
    private val repository: DeviceRepository
) {
    operator fun invoke(): Flow<List<com.msa.msahub.features.devices.domain.model.Device>> {
        return repository.observeDevices()
    }
}
