package com.msa.msahub.features.devices.data.realtime

import com.msa.msahub.features.devices.data.local.dao.DeviceHistoryDao
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.entity.DeviceHistoryEntity
import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttEvent
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttStateParser
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceStatusEvent
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID

class DeviceRealtimeBridge(
    private val mqttHandler: DeviceMqttHandler,
    private val parser: DeviceMqttStateParser,
    private val deviceStateDao: DeviceStateDao,
    private val deviceHistoryDao: DeviceHistoryDao
) {

    suspend fun run(deviceId: String) {
        mqttHandler.subscribeToState(deviceId)

        mqttHandler.observeState(deviceId).collectLatest { event ->
            if (event is DeviceStatusEvent) {
                // Here we might need the actual payload if DeviceStatusEvent doesn't have it parsed
                // but the observer in Handler already maps to DeviceStatusEvent.
                // If it needs raw parsing:
                val parsed = event // Already parsed in handler's observeState mapping
                
                val stateId = UUID.randomUUID().toString()
                val entity = DeviceStateEntity(
                    id = stateId,
                    deviceId = parsed.deviceId,
                    isOnline = parsed.online,
                    isOn = true, // Default or parse from map
                    brightness = null,
                    temperatureC = null,
                    humidityPercent = null,
                    batteryPercent = null,
                    updatedAtMillis = parsed.timestamp
                )
                deviceStateDao.upsert(entity)

                deviceHistoryDao.insert(
                    DeviceHistoryEntity(
                        id = UUID.randomUUID().toString(),
                        deviceId = parsed.deviceId,
                        stateId = stateId,
                        recordedAtMillis = parsed.timestamp
                    )
                )
            }
        }
    }
}
