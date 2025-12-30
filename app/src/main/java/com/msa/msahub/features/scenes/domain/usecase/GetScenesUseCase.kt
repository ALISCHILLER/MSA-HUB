package com.msa.msahub.features.scenes.domain.usecase

import com.msa.msahub.features.scenes.domain.model.Scene
import com.msa.msahub.features.scenes.domain.repository.SceneRepository
import kotlinx.coroutines.flow.Flow

class GetScenesUseCase(
    private val repo: SceneRepository
) {
    operator fun invoke(): Flow<List<Scene>> = repo.observeScenes()
}
