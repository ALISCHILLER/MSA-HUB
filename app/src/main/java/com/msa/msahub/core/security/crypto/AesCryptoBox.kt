package com.msa.msahub.core.security.crypto

import com.msa.msahub.core.security.keys.AndroidKeyManager
import java.nio.ByteBuffer
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

class AesCryptoBox(private val keyManager: AndroidKeyManager) : CryptoBox {

    private val algorithm = "AES/GCM/NoPadding"
    private val tagLength = 128
    private val ivLength = 12

    override fun encrypt(data: ByteArray, keyAlias: String): ByteArray {
        val cipher = Cipher.getInstance(algorithm)
        val secretKey = keyManager.getSecretKey(keyManager.getOrCreateKey(keyAlias))
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val iv = cipher.iv
        val encrypted = cipher.doFinal(data)
        
        return ByteBuffer.allocate(iv.size + encrypted.size)
            .put(iv)
            .put(encrypted)
            .array()
    }

    override fun decrypt(encryptedData: ByteArray, keyAlias: String): ByteArray {
        val buffer = ByteBuffer.wrap(encryptedData)
        val iv = ByteArray(ivLength)
        buffer.get(iv)
        val encrypted = ByteArray(buffer.remaining())
        buffer.get(encrypted)

        val cipher = Cipher.getInstance(algorithm)
        val secretKey = keyManager.getSecretKey(keyAlias)
        val spec = GCMParameterSpec(tagLength, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        
        return cipher.doFinal(encrypted)
    }
}
