package com.msa.msahub.features.devices.data.mapper

import com.msa.msahub.features.devices.data.local.entity.DeviceEntity
import com.msa.msahub.features.devices.data.remote.model.DeviceRemoteModel
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceStatusEvent
import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.devices.domain.model.DeviceCapability
import com.msa.msahub.features.devices.domain.model.DeviceType

class DeviceMapper {

    fun toEntity(device: Device): DeviceEntity {
        return DeviceEntity(
            id = device.id,
            name = device.name,
            type = device.type.name,
            capabilitiesCsv = device.capabilities.joinToString(",") { it.name },
            isFavorite = device.isFavorite,
            roomName = device.roomName,
            lastSeenMillis = device.lastSeenMillis
        )
    }

    fun fromEntity(entity: DeviceEntity): Device {
        val caps = entity.capabilitiesCsv
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { runCatching { DeviceCapability.valueOf(it) }.getOrNull() }
            .toSet()

        return Device(
            id = entity.id,
            name = entity.name,
            type = runCatching { DeviceType.valueOf(entity.type) }.getOrDefault(DeviceType.UNKNOWN),
            capabilities = caps,
            isFavorite = entity.isFavorite,
            roomName = entity.roomName,
            lastSeenMillis = entity.lastSeenMillis
        )
    }

    fun fromRemote(model: DeviceRemoteModel): Device {
        val caps = model.capabilities
            .mapNotNull { runCatching { DeviceCapability.valueOf(it) }.getOrNull() }
            .toSet()

        return Device(
            id = model.id,
            name = model.name,
            type = runCatching { DeviceType.valueOf(model.type) }.getOrDefault(DeviceType.UNKNOWN),
            capabilities = caps,
            isFavorite = false,
            roomName = model.room,
            lastSeenMillis = model.lastSeen
        )
    }

    fun toRemote(device: Device): DeviceRemoteModel {
        return DeviceRemoteModel(
            id = device.id,
            name = device.name,
            type = device.type.name,
            capabilities = device.capabilities.map { it.name },
            room = device.roomName,
            lastSeen = device.lastSeenMillis
        )
    }
}
