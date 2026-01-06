package com.msa.msahub.features.scenes.data.mapper

import com.msa.msahub.features.scenes.data.local.entity.SceneEntity
import com.msa.msahub.features.scenes.domain.model.Scene
import com.msa.msahub.features.scenes.domain.model.SceneAction
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SceneMapper(private val json: Json) {
    fun toDomain(entity: SceneEntity): Scene = Scene(
        id = entity.id,
        name = entity.name,
        actions = try { json.decodeFromString<List<SceneAction>>(entity.actions) } catch (e: Exception) { emptyList() },
        enabled = entity.enabled
    )

    fun toEntity(scene: Scene, now: Long): SceneEntity = SceneEntity(
        id = scene.id,
        name = scene.name,
        enabled = scene.enabled,
        actions = json.encodeToString(scene.actions),
        updatedAt = now
    )
}
