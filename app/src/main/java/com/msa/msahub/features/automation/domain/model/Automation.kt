package com.msa.msahub.features.automation.domain.model

data class Automation(
    val id: String,
    val name: String,
    val enabled: Boolean = true,
    val trigger: AutomationTrigger,
    val actions: List<String>, // لیست ID صحنه‌هایی که باید اجرا شوند
    val createdAt: Long = System.currentTimeMillis()
)

sealed interface AutomationTrigger {
    data class DeviceState(
        val deviceId: String,
        val key: String, // مثلاً "temperature" یا "motion"
        val operator: Operator,
        val value: String
    ) : AutomationTrigger

    data class Time(
        val hour: Int,
        val minute: Int,
        val daysOfWeek: List<Int>
    ) : AutomationTrigger
}

enum class Operator {
    EQUALS, GREATER_THAN, LESS_THAN, CONTAINS
}
