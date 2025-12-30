package com.msa.msahub.features.devices.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "offline_commands",
    indices = [
        Index(value = ["deviceId"]),
        Index(value = ["createdAtMillis"])
    ]
)
data class OfflineCommandEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val topic: String,
    val payloadBase64: String,
    val qos: Int,
    val retained: Boolean,
    val attempts: Int = 0,
    val lastError: String? = null,
    val createdAtMillis: Long
)
