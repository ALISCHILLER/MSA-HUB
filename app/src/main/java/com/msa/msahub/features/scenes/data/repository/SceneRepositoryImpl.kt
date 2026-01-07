package com.msa.msahub.features.scenes.data.repository

import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Clock
import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.domain.model.DeviceCommand
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import com.msa.msahub.features.scenes.data.local.dao.SceneDao
import com.msa.msahub.features.scenes.data.mapper.SceneMapper
import com.msa.msahub.features.scenes.domain.model.Scene
import com.msa.msahub.features.scenes.domain.repository.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class SceneRepositoryImpl(
    private val dao: SceneDao,
    private val mapper: SceneMapper,
    private val deviceRepository: DeviceRepository,
    private val clock: Clock
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

    /**
     * Patch Applied: 
     * Now using deviceRepository.sendCommand instead of direct MQTT/DAO calls.
     * This ensures scenes benefit from:
     * 1. Standard JSON payload (with commandId)
     * 2. Proper Outbox queueing with correlationId
     * 3. Consistent error logging
     */
    override suspend fun executeScene(sceneId: String): Result<Unit> {
        val scene = when (val r = getScene(sceneId)) {
            is Result.Failure -> return r
            is Result.Success -> r.data
        }

        return try {
            scene.actions.forEach { action ->
                val command = DeviceCommand(
                    deviceId = action.deviceId,
                    action = action.command,
                    // If your SceneAction has a specific params map, use it here.
                    // For now, mapping action.payload to a param named 'raw' if needed,
                    // but preferably scenes should use structured params.
                    params = action.params ?: emptyMap(),
                    createdAtMillis = clock.nowMillis()
                )
                
                deviceRepository.sendCommand(command)
            }

            Result.Success(Unit)
        } catch (t: Throwable) {
            Result.Failure(AppError.Unknown("Failed to execute scene", t))
        }
    }
}
