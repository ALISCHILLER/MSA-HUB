package com.msa.msahub.features.devices.domain.usecase

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.devices.domain.repository.DeviceRepository

class GetDeviceDetailUseCase(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(deviceId: String, forceRefresh: Boolean = false): Result<Device> {
        return repository.getDeviceDetail(deviceId, forceRefresh)
    }
}
