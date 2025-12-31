package com.msa.msahub.features.devices.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "offline_commands",
    indices = [
        Index(value = ["deviceId"]),
        Index(value = ["createdAtMillis"]),
        Index(value = ["status"]),
        Index(value = ["attempts"])
    ]
)
data class OfflineCommandEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val topic: String,
    val payloadBase64: String,
    val qos: Int,
    val retained: Boolean,
    val status: OfflineCommandStatus = OfflineCommandStatus.PENDING,
    val correlationId: String? = null,
    val attempts: Int = 0,
    val maxAttempts: Int = 5,
    val lastError: String? = null,
    val lastAttemptAtMillis: Long? = null,
    val createdAtMillis: Long,
    val updatedAtMillis: Long = createdAtMillis
)
