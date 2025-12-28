package com.msa.msahub.core.platform.storage

interface PreferencesManager {
    suspend fun saveString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun clear()
}
