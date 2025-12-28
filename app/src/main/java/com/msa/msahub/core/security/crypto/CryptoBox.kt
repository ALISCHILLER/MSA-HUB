package com.msa.msahub.core.security.crypto

interface CryptoBox {
    fun encrypt(data: ByteArray, keyAlias: String): ByteArray
    fun decrypt(encryptedData: ByteArray, keyAlias: String): ByteArray
}
