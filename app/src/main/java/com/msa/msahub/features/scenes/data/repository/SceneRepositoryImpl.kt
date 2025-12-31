package com.msa.msahub.features.scenes.data.repository

import android.util.Base64
import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Clock
import com.msa.msahub.core.common.IdGenerator
import com.msa.msahub.core.common.Result
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttMessage
import com.msa.msahub.core.platform.network.mqtt.Qos
import com.msa.msahub.features.devices.data.local.dao.OfflineCommandDao
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandEntity
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttTopics
import com.msa.msahub.features.scenes.data.local.dao.SceneDao
import com.msa.msahub.features.scenes.data.mapper.SceneMapper
import com.msa.msahub.features.scenes.domain.model.Scene
import com.msa.msahub.features.scenes.domain.repository.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SceneRepositoryImpl(
    private val dao: SceneDao,
    private val mapper: SceneMapper,
    private val mqtt: MqttClient,
    private val offlineDao: OfflineCommandDao,
    private val clock: Clock,
    private val ids: IdGenerator
) : SceneRepository {

    override fun observeScenes(): Flow<List<Scene>> {
        return dao.observeScenes().map { list -> list.map(mapper::toDomain) }
    }

    override suspend fun getScene(sceneId: String): Result<Scene> {
        return try {
            val entity = dao.getScene(sceneId) ?: return Result.Failure(AppError.Validation("Scene not found"))
            Result.Success(mapper.toDomain(entity))
        } catch (t: Throwable) {
            Result.Failure(AppError.Database("Failed to load scene", t))
        }
    }

    override suspend fun upsertScene(scene: Scene): Result<Unit> {
        return try {
            dao.upsert(mapper.toEntity(scene, clock.nowMillis()))
            Result.Success(Unit)
        } catch (t: Throwable) {
            Result.Failure(AppError.Database("Failed to save scene", t))
        }
    }

    override suspend fun deleteScene(sceneId: String): Result<Unit> {
        return try {
            dao.delete(sceneId)
            Result.Success(Unit)
        } catch (t: Throwable) {
            Result.Failure(AppError.Database("Failed to delete scene", t))
        }
    }

    override suspend fun executeScene(sceneId: String): Result<Unit> {
        val scene = when (val r = getScene(sceneId)) {
            is Result.Failure -> return r
            is Result.Success -> r.data
        }

        return try {
            val isConnected = mqtt.connectionState.value.toString().contains("Connected", ignoreCase = true)

            scene.actions.forEach { action ->
                val topic = DeviceMqttTopics.commandTopic(action.deviceId)
                val payloadBytes = (action.payload ?: action.command).toByteArray()

                if (isConnected) {
                    mqtt.publish(MqttMessage(topic = topic, payload = payloadBytes))
                } else {
                    offlineDao.upsert(
                        OfflineCommandEntity(
                            id = ids.uuid(),
                            deviceId = action.deviceId,
                            topic = topic,
                            payloadBase64 = Base64.encodeToString(payloadBytes, Base64.NO_WRAP),
                            qos = 1, // Default to AtLeastOnce
                            retained = false,
                            createdAtMillis = clock.nowMillis()
                        )
                    )
                }
            }

            Result.Success(Unit)
        } catch (t: Throwable) {
            Result.Failure(AppError.Unknown("Failed to execute scene", t))
        }
    }
}
