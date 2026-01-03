package com.msa.msahub.features.scenes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey val id: String,
    val name: String,
    val enabled: Boolean,
    val actions: String,
    val updatedAt: Long
)
