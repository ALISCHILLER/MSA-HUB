package com.msa.msahub.core.storage

interface PreferencesManager {
    suspend fun saveString(key: String, value: String)
    suspend fun getString(key: String): String?
}
