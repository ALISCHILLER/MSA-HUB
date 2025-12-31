package com.msa.msahub.core.platform.database

import com.msa.msahub.BuildConfig
import com.msa.msahub.core.common.Logger
import com.msa.msahub.features.devices.data.local.dao.DeviceDao
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.entity.DeviceEntity
import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DatabaseInitializer(
    private val deviceDao: DeviceDao,
    private val deviceStateDao: DeviceStateDao,
    private val logger: Logger,
    private val scope: CoroutineScope
) {
    fun seedIfNeeded() {
        if (!BuildConfig.DEBUG) {
            logger.d("Production build detected, skipping database seeding.")
            return
        }

        scope.launch {
            runCatching {
                val count = deviceDao.count()
                if (count > 0) {
                    logger.d("Database already has $count devices, skipping seed")
                    return@launch
                }

                logger.d("Seeding database with initial devices (DEBUG MODE ONLY)")

                val now = System.currentTimeMillis()
                
                val sampleDevices = listOf(
                    DeviceEntity(
                        id = "device_001",
                        name = "Living Room Light",
                        type = "LIGHT",
                        capabilitiesCsv = "ON_OFF,DIMMING",
                        isFavorite = false,
                        roomName = "Living Room",
                        lastSeenMillis = now
                    ),
                    DeviceEntity(
                        id = "device_002",
                        name = "Thermostat",
                        type = "THERMOSTAT",
                        capabilitiesCsv = "TEMPERATURE",
                        isFavorite = true,
                        roomName = "Bedroom",
                        lastSeenMillis = now
                    )
                )

                deviceDao.upsertAll(sampleDevices)

                val sampleStates = listOf(
                    DeviceStateEntity(
                        id = "state_001",
                        deviceId = "device_001",
                        isOnline = true,
                        isOn = true,
                        brightness = 75,
                        temperatureC = null,
                        humidityPercent = null,
                        batteryPercent = null,
                        updatedAtMillis = now
                    ),
                    DeviceStateEntity(
                        id = "state_002",
                        deviceId = "device_002",
                        isOnline = true,
                        isOn = false,
                        brightness = null,
                        temperatureC = 22.5,
                        humidityPercent = 45.0,
                        batteryPercent = 90,
                        updatedAtMillis = now
                    )
                )

                sampleStates.forEach { deviceStateDao.upsert(it) }

                logger.i("Database seeded successfully")
            }.onFailure { e ->
                logger.e("Failed to seed database", e)
            }
        }
    }
}
