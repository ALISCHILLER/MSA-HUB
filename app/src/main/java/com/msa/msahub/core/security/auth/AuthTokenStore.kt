package com.msa.msahub.core.security.auth

import kotlinx.coroutines.runBlocking

interface AuthTokenStore {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()

    /**
     * دریافت توکن به صورت همگام (Synchronous) برای استفاده در اینترسپتورها
     */
    fun getTokenSync(): String? = runBlocking { getToken() }
}
