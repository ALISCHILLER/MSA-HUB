package com.msa.msahub.core.common

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

object JsonExt {
    fun parseObjectOrNull(json: Json, raw: String): JsonObject? {
        return try {
            val el: JsonElement = json.parseToJsonElement(raw)
            el.jsonObject
        } catch (_: Throwable) {
            null
        }
    }

    fun truncate(raw: String, max: Int = 2000): String =
        if (raw.length <= max) raw else raw.take(max) + "…(truncated)"
}
