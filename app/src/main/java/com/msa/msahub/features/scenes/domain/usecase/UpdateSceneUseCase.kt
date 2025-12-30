package com.msa.msahub.features.scenes.domain.usecase

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.scenes.domain.model.Scene
import com.msa.msahub.features.scenes.domain.repository.SceneRepository

class UpdateSceneUseCase(
    private val repo: SceneRepository,
    private val validate: ValidateSceneUseCase
) {
    suspend operator fun invoke(scene: Scene): Result<Unit> {
        return when (val v = validate(scene)) {
            is Result.Failure -> v
            is Result.Success -> repo.upsertScene(scene)
        }
    }
}
