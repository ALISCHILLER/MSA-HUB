package com.msa.msahub.features.scenes.domain.repository

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.scenes.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeScenes(): Flow<List<Scene>>
    suspend fun getScene(sceneId: String): Result<Scene>
    suspend fun upsertScene(scene: Scene): Result<Unit>
    suspend fun deleteScene(sceneId: String): Result<Unit>
    suspend fun executeScene(sceneId: String): Result<Unit>
}
