package com.msa.msahub.features.devices.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_commands")
data class OfflineCommandEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val topic: String,
    val payload: ByteArray,
    val qos: Int,
    val retained: Boolean,
    val status: String,
    val attempts: Int,
    val lastError: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_SENT = "SENT"
    }
}
