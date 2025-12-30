package com.msa.msahub.features.scenes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val actionsJson: String,
    val createdAt: Long,
    val updatedAt: Long
)
