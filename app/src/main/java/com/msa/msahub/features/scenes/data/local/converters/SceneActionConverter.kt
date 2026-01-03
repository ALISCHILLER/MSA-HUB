package com.msa.msahub.features.scenes.data.local.converters

import androidx.room.TypeConverter
import com.msa.msahub.core.common.JsonProvider
import com.msa.msahub.features.scenes.domain.model.SceneAction
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString

class SceneActionConverter {
    private val json = JsonProvider.json
    private val serializer = ListSerializer(SceneAction.serializer())

    @TypeConverter
    fun fromString(value: String?): List<SceneAction> {
        return if (value == null) emptyList() else json.decodeFromString(serializer, value)
    }

    @TypeConverter
    fun toString(value: List<SceneAction>): String {
        return json.encodeToString(serializer, value)
    }
}
