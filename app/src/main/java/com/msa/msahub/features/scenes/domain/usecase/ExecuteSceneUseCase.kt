package com.msa.msahub.features.scenes.domain.usecase

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.scenes.domain.repository.SceneRepository

class ExecuteSceneUseCase(
    private val repo: SceneRepository
) {
    suspend operator fun invoke(sceneId: String): Result<Unit> = repo.executeScene(sceneId)
}
