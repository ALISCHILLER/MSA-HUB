package com.msa.msahub.features.automation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val enabled: Boolean,
    val triggerType: String, // "DEVICE" or "TIME"
    val triggerConfigJson: String, // تنظیمات شرط به صورت JSON
    val sceneIdsJson: String, // لیست ID صحنه‌ها به صورت JSON
    val createdAt: Long
)
