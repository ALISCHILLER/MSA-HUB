package com.msa.msahub.features.devices.data.mapper

import com.msa.msahub.features.devices.data.local.entity.DeviceHistoryEntity
import com.msa.msahub.features.devices.domain.model.DeviceHistoryItem

class DeviceHistoryMapper {

    fun toDomain(entity: DeviceHistoryEntity): DeviceHistoryItem {
        return DeviceHistoryItem(
            id = entity.id,
            deviceId = entity.deviceId,
            eventType = "STATE_SNAPSHOT",
            details = "stateId=${entity.stateId}",
            timestamp = entity.recordedAtMillis
        )
    }

    fun toEntity(domain: DeviceHistoryItem): DeviceHistoryEntity {
        val stateId = domain.details.substringAfter("stateId=").ifBlank { "unknown" }
        return DeviceHistoryEntity(
            id = domain.id,
            deviceId = domain.deviceId,
            stateId = stateId,
            recordedAtMillis = domain.timestamp
        )
    }
}
