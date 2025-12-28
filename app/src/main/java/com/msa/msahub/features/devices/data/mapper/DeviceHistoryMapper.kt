package com.msa.msahub.features.devices.data.mapper

import com.msa.msahub.features.devices.data.local.entity.DeviceHistoryEntity
import com.msa.msahub.features.devices.domain.model.DeviceHistoryItem

object DeviceHistoryMapper {
    fun toDomain(e: DeviceHistoryEntity): DeviceHistoryItem =
        DeviceHistoryItem(
            id = e.id,
            deviceId = e.deviceId,
            eventType = e.eventType,
            details = e.details,
            timestamp = e.timestamp
        )

    fun toEntity(d: DeviceHistoryItem): DeviceHistoryEntity =
        DeviceHistoryEntity(
            id = d.id,
            deviceId = d.deviceId,
            eventType = d.eventType,
            details = d.details,
            timestamp = d.timestamp
        )
}
