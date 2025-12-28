package com.msa.msahub.features.devices.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_history")
data class DeviceHistoryEntity(
    @PrimaryKey val id: String,          // historyId (uuid)
    val deviceId: String,
    val stateId: String,                 // references DeviceStateEntity.id logically
    val recordedAtMillis: Long
)
