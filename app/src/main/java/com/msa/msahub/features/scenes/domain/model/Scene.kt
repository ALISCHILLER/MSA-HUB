package com.msa.msahub.features.scenes.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Scene(
    val id: String,
    val name: String,
    val actions: List<SceneAction>,
    val enabled: Boolean = true
)

@Serializable
data class SceneAction(
    val deviceId: String,
    val command: String,
    val payload: String? = null
)
