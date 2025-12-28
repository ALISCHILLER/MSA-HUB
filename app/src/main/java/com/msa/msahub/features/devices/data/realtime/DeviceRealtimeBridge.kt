package com.msa.msahub.features.devices.data.realtime

import com.msa.msahub.features.devices.data.local.dao.DeviceHistoryDao
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.entity.DeviceHistoryEntity
import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttEvent
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttStateParser
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID

class DeviceRealtimeBridge(
    private val mqttHandler: DeviceMqttHandler,
    private val parser: DeviceMqttStateParser,
    private val deviceStateDao: DeviceStateDao,
    private val deviceHistoryDao: DeviceHistoryDao
) {

    /**
     * Call inside a coroutine scope (e.g., viewModelScope.launch).
     * Cancelling the coroutine will stop realtime collection.
     */
    suspend fun run(deviceId: String) {
        mqttHandler.subscribeToState(deviceId)

        mqttHandler.observeState(deviceId).collectLatest { event ->
            when (event) {
                is DeviceMqttEvent.StateUpdated -> {
                    val parsed = parser.parse(event.payload) ?: return@collectLatest

                    val stateId = UUID.randomUUID().toString()
                    val entity = DeviceStateEntity(
                        id = stateId,
                        deviceId = parsed.deviceId,
                        isOnline = parsed.isOnline,
                        isOn = parsed.isOn,
                        brightness = parsed.brightness,
                        temperatureC = parsed.temperatureC,
                        humidityPercent = parsed.humidityPercent,
                        batteryPercent = parsed.batteryPercent,
                        updatedAtMillis = parsed.updatedAtMillis
                    )
                    deviceStateDao.upsert(entity)

                    deviceHistoryDao.upsert(
                        DeviceHistoryEntity(
                            id = UUID.randomUUID().toString(),
                            deviceId = parsed.deviceId,
                            stateId = stateId,
                            recordedAtMillis = parsed.updatedAtMillis
                        )
                    )
                }
            }
        }
    }
}
