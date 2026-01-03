package com.msa.msahub.features.devices.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val capabilitiesCsv: String,
    val isFavorite: Boolean,
    val roomName: String?,
    val lastSeenMillis: Long,
    val updatedAtMillis: Long = System.currentTimeMillis()
)
