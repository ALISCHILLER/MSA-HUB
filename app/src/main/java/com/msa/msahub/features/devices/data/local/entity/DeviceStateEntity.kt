package com.msa.msahub.features.devices.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_states")
data class DeviceStateEntity(
    @PrimaryKey val id: String,          // stateId (uuid)
    val deviceId: String,
    val isOnline: Boolean,
    val isOn: Boolean?,
    val brightness: Int?,
    val temperatureC: Double?,
    val humidityPercent: Double?,
    val batteryPercent: Int?,
    val updatedAtMillis: Long
)
