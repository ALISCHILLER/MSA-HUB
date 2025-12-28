package com.msa.msahub.core.security.auth

interface AuthTokenStore {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
}
