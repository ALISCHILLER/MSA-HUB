package com.msa.msahub.features.analytics.data.engine

import com.msa.msahub.core.common.Logger
import com.msa.msahub.features.analytics.data.local.dao.AnalyticsDao
import com.msa.msahub.features.analytics.data.local.entity.SensorAnalyticsEntity
import com.msa.msahub.features.devices.data.local.dao.DeviceDao
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao

class AnalyticsEngine(
    private val deviceDao: DeviceDao,
    private val stateDao: DeviceStateDao,
    private val analyticsDao: AnalyticsDao,
    private val logger: Logger
) {
    suspend fun runDailyAnalysis() {
        logger.i("Running daily sensor analysis...")

        val now = System.currentTimeMillis()
        val cutoff24h = now - 24L * 60L * 60L * 1000L

        val devices = deviceDao.getAll()
        logger.i("Analytics: devices=${devices.size}")

        for (d in devices) {
            val recent = stateDao.getRecent(d.id, limit = 500)
                .filter { it.updatedAtMillis >= cutoff24h }

            if (recent.isEmpty()) continue

            suspend fun upsertMetric(metricType: String, values: List<Double>) {
                if (values.isEmpty()) return
                val avg = values.average()
                val max = values.maxOrNull() ?: return
                val min = values.minOrNull() ?: return

                analyticsDao.upsert(
                    SensorAnalyticsEntity(
                        deviceId = d.id,
                        dateMillis = now,
                        metricType = metricType,
                        avgValue = avg,
                        maxValue = max,
                        minValue = min
                    )
                )
            }

            upsertMetric(
                metricType = "TEMPERATURE",
                values = recent.mapNotNull { it.temperatureC }
            )

            upsertMetric(
                metricType = "HUMIDITY",
                values = recent.mapNotNull { it.humidityPercent }
            )

            upsertMetric(
                metricType = "BATTERY",
                values = recent.mapNotNull { it.batteryPercent?.toDouble() }
            )
        }

        val cutoff90d = now - 90L * 24L * 60L * 60L * 1000L
        analyticsDao.deleteOldAnalytics(cutoff90d)

        logger.i("Analytics: daily analysis done.")
    }
}
