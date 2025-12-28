package com.msa.msahub.core.platform.storage

interface CacheManager {
    suspend fun cacheData(key: String, data: String)
    suspend fun getCachedData(key: String): String?
    fun clearCache()
}
