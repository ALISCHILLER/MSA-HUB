package com.msa.msahub.features.analytics.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sensor_analytics",
    indices = [Index(value = ["deviceId", "dateMillis"])]
)
data class SensorAnalyticsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val dateMillis: Long,
    val metricType: String, // "TEMPERATURE", "HUMIDITY", "POWER"
    val avgValue: Double,
    val maxValue: Double,
    val minValue: Double
)
