package com.msa.msahub.features.scenes.presentation.state

import com.msa.msahub.features.scenes.domain.model.Scene
import com.msa.msahub.features.scenes.domain.model.SceneAction

data class SceneListUiState(
    val isLoading: Boolean = true,
    val scenes: List<Scene> = emptyList(),
    val error: String? = null
)

data class SceneEditorUiState(
    val isLoading: Boolean = false,
    val sceneId: String? = null,
    val name: String = "",
    val enabled: Boolean = true,
    val actions: List<SceneAction> = emptyList(),
    val error: String? = null,
    val saved: Boolean = false
)
