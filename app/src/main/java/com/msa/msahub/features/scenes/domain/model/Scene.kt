package com.msa.msahub.features.scenes.domain.model

data class Scene(
    val id: String,
    val name: String,
    val icon: String,
    val actions: List<SceneAction>,
    val isActive: Boolean
)

data class SceneAction(
    val deviceId: String,
    val action: String,
    val params: Map<String, String>
)
