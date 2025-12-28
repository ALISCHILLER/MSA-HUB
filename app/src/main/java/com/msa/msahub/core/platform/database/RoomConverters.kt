package com.msa.msahub.core.platform.database

import androidx.room.TypeConverter

object RoomConverters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(separator = "|") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|").map { it.trim() }.filter { it.isNotBlank() }
    }
}
