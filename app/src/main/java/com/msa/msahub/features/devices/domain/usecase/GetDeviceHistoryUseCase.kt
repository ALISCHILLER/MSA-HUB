package com.msa.msahub.features.devices.domain.usecase

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.model.DeviceState
import com.msa.msahub.features.devices.domain.repository.DeviceRepository

class GetDeviceHistoryUseCase(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(deviceId: String, limit: Int = 50): Result<List<DeviceState>> {
        return repository.getDeviceHistory(deviceId, limit)
    }
}
