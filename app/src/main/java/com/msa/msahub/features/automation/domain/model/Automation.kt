package com.msa.msahub.features.automation.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Automation(
    val id: String,
    val name: String,
    val isEnabled: Boolean = true,
    val trigger: AutomationTrigger,
    val condition: AutomationCondition? = null,
    val actions: List<AutomationAction>
)

@Serializable
sealed interface AutomationTrigger {
    @Serializable
    data class DeviceStateChanged(
        val deviceId: String,
        val attribute: String,
        val from: String? = null,
        val to: String? = null
    ) : AutomationTrigger
    
    @Serializable
    data class TimeSchedule(val cronExpression: String) : AutomationTrigger
}

@Serializable
sealed interface AutomationCondition {
    @Serializable
    data class DeviceAttributeValue(
        val deviceId: String,
        val attribute: String,
        val operator: Operator,
        val value: String
    ) : AutomationCondition

    enum class Operator { EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN }
}

@Serializable
data class AutomationAction(
    val deviceId: String,
    val command: String,
    val params: Map<String, String> = emptyMap()
)
