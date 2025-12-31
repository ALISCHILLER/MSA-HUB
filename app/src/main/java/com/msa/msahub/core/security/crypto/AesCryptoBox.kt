package com.msa.msahub.core.security.crypto

import android.util.Base64
import com.msa.msahub.core.security.keys.KeyManager
import java.nio.ByteBuffer
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

class AesCryptoBox(private val keyManager: KeyManager) : CryptoBox {

    private val algorithm = "AES/GCM/NoPadding"
    private val tagLength = 128
    private val ivLength = 12

    override fun encrypt(data: ByteArray, keyAlias: String): ByteArray {
        val cipher = Cipher.getInstance(algorithm)
        val secretKey = keyManager.getOrCreateSecretKey(keyAlias)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val iv = cipher.iv
        val encrypted = cipher.doFinal(data)
        
        val combined = ByteBuffer.allocate(iv.size + encrypted.size)
            .put(iv)
            .put(encrypted)
            .array()
            
        return Base64.encode(combined, Base64.NO_WRAP)
    }

    override fun decrypt(encryptedData: ByteArray, keyAlias: String): ByteArray {
        val decoded = Base64.decode(encryptedData, Base64.NO_WRAP)
        val buffer = ByteBuffer.wrap(decoded)
        val iv = ByteArray(ivLength)
        buffer.get(iv)
        val encrypted = ByteArray(buffer.remaining())
        buffer.get(encrypted)

        val cipher = Cipher.getInstance(algorithm)
        val secretKey = keyManager.getOrCreateSecretKey(keyAlias)
        val spec = GCMParameterSpec(tagLength, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        
        return cipher.doFinal(encrypted)
    }
}
