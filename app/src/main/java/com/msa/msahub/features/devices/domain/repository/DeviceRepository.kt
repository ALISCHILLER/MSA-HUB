package com.msa.msahub.features.devices.domain.repository

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.model.*
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun observeDevices(): Flow<List<Device>>
    fun observeDevice(deviceId: String): Flow<Device?>
    fun observeDeviceState(deviceId: String): Flow<DeviceState?>
    fun observeDeviceHistory(deviceId: String): Flow<List<DeviceHistoryItem>>
    
    suspend fun syncDevices(): Result<Unit>
    suspend fun sendCommand(command: DeviceCommand): Result<CommandAck>
    suspend fun updateDeviceFavorite(deviceId: String, isFavorite: Boolean): Result<Unit>
    
    // متدهای اضافه شده برای هماهنگی با Workerها و UseCaseها
    suspend fun getDevices(forceRefresh: Boolean = false): Result<List<Device>>
    suspend fun getDeviceDetail(deviceId: String, forceRefresh: Boolean = false): Result<Device>
    suspend fun getDeviceHistory(deviceId: String, limit: Int = 50): Result<List<DeviceState>>
    suspend fun flushOutbox(max: Int = 50): Result<Int>
}
