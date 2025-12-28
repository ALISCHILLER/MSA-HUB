package com.msa.msahub.core.platform.storage

interface SecureStorage {
    suspend fun saveSecret(key: String, value: String)
    suspend fun getSecret(key: String): String?
    suspend fun removeSecret(key: String)
}
