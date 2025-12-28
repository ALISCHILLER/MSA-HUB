package com.msa.msahub.features.devices.domain.usecase

import com.msa.msahub.features.devices.domain.model.DeviceState
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow

class ObserveDeviceStateUseCase(
    private val repository: DeviceRepository
) {
    operator fun invoke(deviceId: String): Flow<DeviceState?> {
        return repository.observeDeviceState(deviceId)
    }
}
