package com.msa.msahub.core.security.keys

interface KeyManager {
    fun getOrCreateKey(alias: String): String
    fun rotateKey(alias: String)
}
