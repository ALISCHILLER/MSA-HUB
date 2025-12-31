package com.msa.msahub.features.devices.data.repository

import android.util.Base64
import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Result
import com.msa.msahub.core.platform.network.mqtt.Qos
import com.msa.msahub.features.devices.data.local.dao.*
import com.msa.msahub.features.devices.data.mapper.*
import com.msa.msahub.features.devices.data.remote.api.DeviceApiService
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttTopics
import com.msa.msahub.features.devices.domain.model.*
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

    override fun observeDevices(): Flow<List<Device>> =
        deviceDao.observeAll().map { list -> list.map(deviceMapper::fromEntity) }

    override fun observeDevice(deviceId: String): Flow<Device?> =
        deviceDao.observeById(deviceId).map { it?.let(deviceMapper::fromEntity) }

    override fun observeDeviceState(deviceId: String): Flow<DeviceState?> =
        deviceStateDao.observeLatest(deviceId).map { it?.let(deviceStateMapper::fromEntity) }

    override fun observeDeviceHistory(deviceId: String): Flow<List<DeviceHistoryItem>> =
        deviceHistoryDao.observeRecent(deviceId, 50).map { list ->
            list.map { DeviceHistoryItem(it.id, it.deviceId, it.recordedAtMillis) }
        }

    override suspend fun getDevices(forceRefresh: Boolean): Result<List<Device>> = try {
        val remote = api.fetchDevices()
        deviceDao.upsertAll(remote.map(deviceMapper::toEntity))
        Result.Success(remote)
    } catch (t: Throwable) {
        val cached = deviceDao.getAll().map(deviceMapper::fromEntity)
        if (cached.isNotEmpty()) Result.Success(cached)
        else Result.Failure(AppError.Unknown("Failed to load devices", t))
    }

    override suspend fun getDeviceDetail(deviceId: String, forceRefresh: Boolean): Result<Device> = try {
        val remote = api.fetchDeviceDetail(deviceId)
        if (remote != null) {
            deviceDao.upsert(deviceMapper.toEntity(remote))
            Result.Success(remote)
        } else Result.Failure(AppError.Unknown("Device not found"))
    } catch (t: Throwable) {
        val cached = deviceDao.getById(deviceId)?.let(deviceMapper::fromEntity)
        cached?.let { Result.Success(it) } ?: Result.Failure(AppError.Unknown("Load failed", t))
    }

    override suspend fun getDeviceHistory(deviceId: String, limit: Int): Result<List<DeviceState>> = try {
        val entities = deviceStateDao.getRecent(deviceId, limit)
        Result.Success(entities.map(deviceStateMapper::fromEntity))
    } catch (t: Throwable) {
        Result.Failure(AppError.Database("Failed to load history", t))
    }

    override suspend fun sendCommand(command: DeviceCommand): Result<CommandAck> = try {
        val topic = DeviceMqttTopics.commandTopic(command.deviceId)
        val payload = commandMapper.toMqttPayload(command)
        val publishOk = runCatching {
            mqttHandler.publishCommand(topic, payload, Qos.AtLeastOnce, false)
        }.isSuccess

        if (publishOk) Result.Success(CommandAck.Success)
        else {
            offlineCommandDao.upsert(
                commandMapper.toOfflineEntity(
                    UUID.randomUUID().toString(),
                    command.deviceId,
                    topic,
                    payload,
                    Qos.AtLeastOnce,
                    false,
                    command.createdAtMillis
                )
            )
            Result.Success(CommandAck.QueuedOffline)
        }
    } catch (t: Throwable) {
        Result.Failure(AppError.Mqtt("Send failed", t))
    }

    override suspend fun flushOutbox(max: Int): Result<Int> = try {
        val pending = offlineCommandDao.getAllPending().take(max)
        pending.forEach {
            val payload = Base64.decode(it.payloadBase64, Base64.NO_WRAP)
            val qos = when (it.qos) {
                0 -> Qos.AtMostOnce
                1 -> Qos.AtLeastOnce
                2 -> Qos.ExactlyOnce
                else -> Qos.AtLeastOnce
            }

            mqttHandler.publishCommand(it.topic, payload, qos, it.retained)
            offlineCommandDao.delete(it.id)
        }
        Result.Success(pending.size)
    } catch (t: Throwable) {
        Result.Failure(AppError.Database("Flush failed", t))
    }
}
