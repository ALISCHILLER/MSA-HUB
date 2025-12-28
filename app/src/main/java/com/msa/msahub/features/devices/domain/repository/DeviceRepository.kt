package com.msa.msahub.features.devices.domain.repository

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.model.CommandAck
import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.devices.domain.model.DeviceCommand
import com.msa.msahub.features.devices.domain.model.DeviceHistoryItem
import com.msa.msahub.features.devices.domain.model.DeviceState
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun observeDevices(): Flow<List<Device>>
    fun observeDevice(deviceId: String): Flow<Device?>
    fun observeDeviceState(deviceId: String): Flow<DeviceState?>

    fun observeDeviceHistory(deviceId: String): Flow<List<DeviceHistoryItem>>

    suspend fun sendCommand(command: DeviceCommand): Result<CommandAck>
    suspend fun flushOutbox(max: Int): Result<Int>
}
