package com.msa.msahub.core.security.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthTokenStore {
    val tokenState: StateFlow<String?>

    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()

    /**
     * Getter همگام و بدون بلاک برای استفاده در interceptor/defaultRequest
     */
    fun peekToken(): String? = tokenState.value
}
