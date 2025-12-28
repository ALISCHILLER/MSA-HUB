package com.msa.msahub.features.devices.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_commands")
data class OfflineCommandEntity(
    @PrimaryKey val id: String,          // commandId (uuid)
    val deviceId: String,
    val topic: String,
    val payloadBase64: String,
    val qos: Int,
    val retained: Boolean,
    val createdAtMillis: Long,
    val attempts: Int,
    val lastError: String?
)
