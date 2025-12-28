package com.msa.msahub.features.devices.data.mapper

import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
import com.msa.msahub.features.devices.domain.model.DeviceState

class DeviceStateMapper {
    fun fromEntity(entity: DeviceStateEntity): DeviceState {
        return DeviceState(
            deviceId = entity.deviceId,
            isOnline = entity.isOnline,
            isOn = entity.isOn,
            brightness = entity.brightness,
            temperatureC = entity.temperatureC,
            humidityPercent = entity.humidityPercent,
            batteryPercent = entity.batteryPercent,
            updatedAtMillis = entity.updatedAtMillis
        )
    }
}
