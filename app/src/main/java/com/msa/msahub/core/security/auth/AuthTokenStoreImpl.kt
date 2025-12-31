package com.msa.msahub.core.security.auth

import com.msa.msahub.core.security.storage.SecurePrefs

class AuthTokenStoreImpl(
    private val securePrefs: SecurePrefs
) : AuthTokenStore {

    private val tokenKey = "auth_token"

    override suspend fun saveToken(token: String) {
        securePrefs.putString(tokenKey, token)
    }

    override suspend fun getToken(): String? {
        return securePrefs.getString(tokenKey)
    }

    override suspend fun clearToken() {
        securePrefs.putString(tokenKey, null)
    }
}
