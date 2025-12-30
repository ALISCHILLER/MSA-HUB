package com.msa.msahub.core.platform.database

import androidx.room.TypeConverter
import com.msa.msahub.core.common.JsonProvider
import com.msa.msahub.features.scenes.domain.model.SceneAction
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

object RoomConverters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if (value.isNullOrEmpty()) "" else value.joinToString("|#|")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|#|").filter { it.isNotBlank() }
    }

    @TypeConverter
    fun fromSceneActionList(value: List<SceneAction>?): String {
        if (value.isNullOrEmpty()) return ""
        val serializer = ListSerializer(SceneAction.serializer())
        return JsonProvider.json.encodeToString(serializer, value)
    }

    @TypeConverter
    fun toSceneActionList(value: String?): List<SceneAction> {
        if (value.isNullOrBlank()) return emptyList()
        val serializer = ListSerializer(SceneAction.serializer())
        return JsonProvider.json.decodeFromString(serializer, value)
    }
}
