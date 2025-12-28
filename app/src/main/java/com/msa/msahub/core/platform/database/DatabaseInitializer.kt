package com.msa.msahub.core.platform.database

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
        scope.launch {
            runCatching {
                val count = deviceDao.count()
                if (count > 0) {
                    logger.d("Database already has $count devices, skipping seed")
                    return@launch
                }

                logger.d("Seeding database with initial devices")

                val now = System.currentTimeMillis()
                
                val sampleDevices = listOf(
                    DeviceEntity(
                        id = "device_001",
                        name = "Living Room Light",
                        type = "LIGHT",
                        capabilities = listOf("ON_OFF", "DIMMING"),
                        createdAt = now,
                        updatedAt = now
                    ),
                    DeviceEntity(
                        id = "device_002",
                        name = "Thermostat",
                        type = "THERMOSTAT",
                        capabilities = listOf("TEMPERATURE"),
                        createdAt = now,
                        updatedAt = now
                    ),
                    DeviceEntity(
                        id = "device_003",
                        name = "Front Door Sensor",
                        type = "SENSOR",
                        capabilities = listOf("MOTION"),
                        createdAt = now,
                        updatedAt = now
                    ),
                    DeviceEntity(
                        id = "device_004",
                        name = "Garage Door",
                        type = "LOCK",
                        capabilities = listOf("LOCK_UNLOCK"),
                        createdAt = now,
                        updatedAt = now
                    ),
                    DeviceEntity(
                        id = "device_005",
                        name = "Kitchen Switch",
                        type = "SWITCH",
                        capabilities = listOf("ON_OFF"),
                        createdAt = now,
                        updatedAt = now
                    )
                )

                deviceDao.upsertAll(sampleDevices)

                val sampleStates = listOf(
                    DeviceStateEntity(
                        deviceId = "device_001",
                        isOnline = true,
                        lastSeenAt = now - 10_000,
                        stateJson = """{"brightness": 75, "power": "on"}"""
                    ),
                    DeviceStateEntity(
                        deviceId = "device_002",
                        isOnline = true,
                        lastSeenAt = now - 30_000,
                        stateJson = """{"temperature": 22.5, "mode": "cool"}"""
                    ),
                    DeviceStateEntity(
                        deviceId = "device_003",
                        isOnline = false,
                        lastSeenAt = now - 3_600_000,
                        stateJson = """{"motion": false, "battery": 85}"""
                    ),
                    DeviceStateEntity(
                        deviceId = "device_004",
                        isOnline = true,
                        lastSeenAt = now - 5_000,
                        stateJson = """{"locked": true, "battery": 90}"""
                    ),
                    DeviceStateEntity(
                        deviceId = "device_005",
                        isOnline = true,
                        lastSeenAt = now - 15_000,
                        stateJson = """{"state": "off"}"""
                    )
                )

                sampleStates.forEach { deviceStateDao.upsert(it) }

                logger.i("Database seeded with ${sampleDevices.size} devices and ${sampleStates.size} states")
            }.onFailure { e ->
                logger.e("Failed to seed database", e)
            }
        }
    }
}
