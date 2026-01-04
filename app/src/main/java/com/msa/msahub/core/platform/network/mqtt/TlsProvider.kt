package com.msa.msahub.core.platform.network.mqtt

import javax.net.ssl.SSLContext

class TlsProvider {
    fun sslContextOrNull(): SSLContext? {
        return try {
            SSLContext.getDefault()
        } catch (e: Exception) {
            null
        }
    }

    // Adding compatibility for old calls if needed
    fun buildSslContext(settings: Any?): SSLContext? = sslContextOrNull()
}
