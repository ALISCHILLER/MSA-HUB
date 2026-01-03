package com.msa.msahub.features.devices.data.realtime

import com.msa.msahub.features.devices.data.local.dao.DeviceHistoryDao
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.entity.DeviceHistoryEntity
import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
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
                val parsed = event
                val state = parser.parseFromStateMap(
                    deviceId = parsed.deviceId,
                    online = parsed.online,
                    state = parsed.state,
                    timestamp = parsed.timestamp
                )

                val stateId = UUID.randomUUID().toString()
                val entity = DeviceStateEntity(
                    id = stateId,
                    deviceId = state.deviceId,
                    isOnline = state.isOnline,
                    isOn = state.isOn,
                    brightness = state.brightness,
                    temperatureC = state.temperatureC,
                    humidityPercent = state.humidityPercent,
                    batteryPercent = state.batteryPercent,
                    updatedAtMillis = state.updatedAtMillis
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
