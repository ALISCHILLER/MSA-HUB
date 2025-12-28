package com.msa.msahub.features.devices.data.repository

import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Result
import com.msa.msahub.core.platform.network.mqtt.Qos
import com.msa.msahub.features.devices.data.local.dao.DeviceDao
import com.msa.msahub.features.devices.data.local.dao.DeviceHistoryDao
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.dao.OfflineCommandDao
import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
import com.msa.msahub.features.devices.data.local.entity.DeviceHistoryEntity
import com.msa.msahub.features.devices.data.mapper.DeviceCommandMapper
import com.msa.msahub.features.devices.data.mapper.DeviceMapper
import com.msa.msahub.features.devices.data.mapper.DeviceStateMapper
import com.msa.msahub.features.devices.data.remote.api.DeviceApiService
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttTopics
import com.msa.msahub.features.devices.domain.model.CommandAck
import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.devices.domain.model.DeviceCommand
import com.msa.msahub.features.devices.domain.model.DeviceState
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class DeviceRepositoryImpl(
    private val deviceDao: DeviceDao,
    private val deviceStateDao: DeviceStateDao,
    private val offlineCommandDao: OfflineCommandDao,
    private val deviceHistoryDao: DeviceHistoryDao,
    private val api: DeviceApiService,
    private val mqttHandler: DeviceMqttHandler,
    private val deviceMapper: DeviceMapper,
    private val deviceStateMapper: DeviceStateMapper,
    private val commandMapper: DeviceCommandMapper
) : DeviceRepository {

    override suspend fun getDevices(forceRefresh: Boolean): Result<List<Device>> {
        return try {
            val cached = deviceDao.getAll().map(deviceMapper::fromEntity)
            if (cached.isNotEmpty() && !forceRefresh) {
                return Result.Success(cached)
            }

            val remote = api.fetchDevices()
            deviceDao.upsertAll(remote.map(deviceMapper::toEntity))
            Result.Success(remote)
        } catch (t: Throwable) {
            Result.Failure(AppError.Unknown("Failed to load devices", t))
        }
    }

    override suspend fun getDeviceDetail(deviceId: String, forceRefresh: Boolean): Result<Device> {
        return try {
            val cached = deviceDao.getById(deviceId)?.let(deviceMapper::fromEntity)
            if (cached != null && !forceRefresh) return Result.Success(cached)

            val remote = api.fetchDeviceDetail(deviceId)
            if (remote != null) {
                deviceDao.upsert(deviceMapper.toEntity(remote))
                return Result.Success(remote)
            }

            cached?.let { Result.Success(it) }
                ?: Result.Failure(AppError.Unknown("Device not found"))
        } catch (t: Throwable) {
            Result.Failure(AppError.Unknown("Failed to load device detail", t))
        }
    }

    override fun observeDeviceState(deviceId: String): Flow<DeviceState?> {
        return deviceStateDao.observeLatest(deviceId).map { entity ->
            entity?.let(deviceStateMapper::fromEntity)
        }
    }

    override suspend fun sendCommand(command: DeviceCommand): Result<CommandAck> {
        return try {
            val qos = Qos.AtLeastOnce
            val topic = DeviceMqttTopics.commandTopic(command.deviceId)
            val payload = commandMapper.toMqttPayload(command)

            val isConnected = false 

            if (isConnected) {
                mqttHandler.publishCommand(topic, payload, qos, retained = false)
                Result.Success(CommandAck.Success)
            } else {
                val id = UUID.randomUUID().toString()
                offlineCommandDao.upsert(
                    commandMapper.toOfflineEntity(
                        id = id,
                        deviceId = command.deviceId,
                        topic = topic,
                        payload = payload,
                        qos = qos,
                        retained = false,
                        createdAtMillis = command.createdAtMillis
                    )
                )
                Result.Success(CommandAck.QueuedOffline)
            }
        } catch (t: Throwable) {
            Result.Failure(AppError.Mqtt("Failed to send command", t))
        }
    }

    override suspend fun getDeviceHistory(deviceId: String, limit: Int): Result<List<DeviceState>> {
        return try {
            val stateEntities = deviceStateDao.getRecent(deviceId, limit)
            val states = stateEntities.map(deviceStateMapper::fromEntity)
            Result.Success(states)
        } catch (t: Throwable) {
            Result.Failure(AppError.Database("Failed to read history", t))
        }
    }

    suspend fun upsertIncomingState(state: DeviceState) {
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

        deviceHistoryDao.upsert(
            DeviceHistoryEntity(
                id = UUID.randomUUID().toString(),
                deviceId = state.deviceId,
                stateId = stateId,
                recordedAtMillis = state.updatedAtMillis
            )
        )
    }
}
