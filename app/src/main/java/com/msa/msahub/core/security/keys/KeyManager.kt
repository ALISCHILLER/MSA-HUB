package com.msa.msahub.core.security.keys

import java.security.KeyStore
import javax.crypto.SecretKey

interface KeyManager {
    fun getOrCreateSecretKey(alias: String): SecretKey
    fun deleteKey(alias: String)
    fun isKeyExpired(alias: String): Boolean
}
