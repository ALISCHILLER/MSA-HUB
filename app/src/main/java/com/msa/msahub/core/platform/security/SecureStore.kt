package com.msa.msahub.core.platform.security

interface SecureStore {
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun remove(key: String)
}
