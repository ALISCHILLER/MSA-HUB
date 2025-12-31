package com.msa.msahub.features.devices.domain.repository

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.model.CommandAck
import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.devices.domain.model.DeviceCommand
import com.msa.msahub.features.devices.domain.model.DeviceState
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    suspend fun getDevices(forceRefresh: Boolean): Result<List<Device>>
    suspend fun getDeviceDetail(deviceId: String, forceRefresh: Boolean): Result<Device>
    suspend fun getDeviceHistory(deviceId: String, limit: Int): Result<List<DeviceState>>

    fun observeDeviceState(deviceId: String): Flow<DeviceState?>

    suspend fun sendCommand(command: DeviceCommand): Result<CommandAck>
    suspend fun flushOutbox(max: Int): Result<Int>
}
