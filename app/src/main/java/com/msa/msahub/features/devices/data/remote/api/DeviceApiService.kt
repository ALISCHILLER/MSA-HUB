package com.msa.msahub.features.devices.data.remote.api

import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.devices.domain.model.DeviceCapability
import com.msa.msahub.features.devices.domain.model.DeviceType

interface DeviceApiService {
    suspend fun fetchDevices(): List<Device>
    suspend fun fetchDeviceDetail(deviceId: String): Device?
}

class FakeDeviceApiService : DeviceApiService {
    override suspend fun fetchDevices(): List<Device> {
        val now = System.currentTimeMillis()
        return listOf(
            Device(
                id = "dev-1",
                name = "Living Room Light",
                type = DeviceType.LIGHT,
                capabilities = setOf(DeviceCapability.ON_OFF, DeviceCapability.DIMMING),
                isFavorite = true,
                roomName = "Living Room",
                lastSeenMillis = now
            ),
            Device(
                id = "dev-2",
                name = "Bedroom Sensor",
                type = DeviceType.SENSOR,
                capabilities = setOf(DeviceCapability.MOTION, DeviceCapability.BATTERY),
                isFavorite = false,
                roomName = "Bedroom",
                lastSeenMillis = now
            )
        )
    }

    override suspend fun fetchDeviceDetail(deviceId: String): Device? {
        return fetchDevices().firstOrNull { it.id == deviceId }
    }
}
