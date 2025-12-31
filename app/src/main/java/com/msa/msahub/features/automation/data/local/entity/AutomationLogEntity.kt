package com.msa.msahub.features.automation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automation_logs")
data class AutomationLogEntity(
    @PrimaryKey val id: String,
    val automationId: String,
    val automationName: String,
    val status: String, // SUCCESS, FAILED
    val detail: String,
    val timestamp: Long = System.currentTimeMillis()
)
