package com.msa.msahub.core.common

import kotlinx.serialization.json.Json

object JsonProvider {
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }
}
