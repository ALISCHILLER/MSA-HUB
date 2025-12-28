package com.msa.msahub.features.devices.domain.usecase

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.devices.domain.repository.DeviceRepository

class GetDevicesUseCase(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<Device>> {
        return repository.getDevices(forceRefresh)
    }
}
