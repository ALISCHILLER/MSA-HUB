package com.msa.msahub.core.security.auth

import com.msa.msahub.core.security.storage.SecurePrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthTokenStoreImpl(
    private val securePrefs: SecurePrefs,
    private val appScope: CoroutineScope
) : AuthTokenStore {

    private val tokenKey = "auth_token"

    private val _tokenState = MutableStateFlow<String?>(null)
    override val tokenState: StateFlow<String?> = _tokenState

    init {
        // load token once at startup (no runBlocking)
        appScope.launch {
            _tokenState.value = securePrefs.getString(tokenKey)
        }
    }

    override suspend fun saveToken(token: String) {
        securePrefs.putString(tokenKey, token)
        _tokenState.value = token
    }

    override suspend fun getToken(): String? = securePrefs.getString(tokenKey)

    override suspend fun clearToken() {
        securePrefs.putString(tokenKey, null)
        _tokenState.value = null
    }
}
