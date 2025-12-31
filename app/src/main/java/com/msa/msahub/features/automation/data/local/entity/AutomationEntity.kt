package com.msa.msahub.features.automation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isEnabled: Boolean,
    val triggerJson: String,    // ذخیره AutomationTrigger به صورت JSON
    val conditionJson: String?, // ذخیره AutomationCondition به صورت JSON
    val actionsJson: String,    // ذخیره List<AutomationAction> به صورت JSON
    val createdAt: Long = System.currentTimeMillis()
)
