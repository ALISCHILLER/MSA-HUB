package com.msa.msahub.core.common

import kotlinx.serialization.json.Json

object JsonProvider {
    val json: Json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
        encodeDefaults = true
    }
}
