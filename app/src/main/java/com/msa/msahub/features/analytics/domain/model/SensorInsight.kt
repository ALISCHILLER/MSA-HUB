package com.msa.msahub.features.analytics.domain.model

data class SensorInsight(
    val deviceId: String,
    val dateMillis: Long,
    val averageValue: Double,
    val maxValue: Double,
    val minValue: Double,
    val unit: String
)

data class UsageInsight(
    val deviceId: String,
    val onTimeMinutes: Long,
    val estimatedEnergyKwh: Double? = null
)
