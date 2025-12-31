package com.msa.msahub.core.security.keys

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * مدیریت صنعتی کلیدها با استفاده از Android KeyStore برای امنیت سطح سخت‌افزار.
 */
class AndroidKeyManager : KeyManager {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    override fun getOrCreateSecretKey(alias: String): SecretKey {
        val entry = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        return entry?.secretKey ?: generateKey(alias)
    }

    override fun deleteKey(alias: String) {
        keyStore.deleteEntry(alias)
    }

    override fun isKeyExpired(alias: String): Boolean {
        // در اندروید KeyStore معمولاً کلیدها منقضی نمی‌شوند مگر اینکه به صورت دستی تنظیم شود
        return !keyStore.containsAlias(alias)
    }

    private fun generateKey(alias: String): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false) // برای عملیات پس‌زمینه (Worker) باید false باشد
            .build()
        
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
}
