package com.msa.msahub.core.platform.network.mqtt

import javax.net.ssl.SSLContext

class TlsProvider {
    /**
     * ساده‌ترین API برای فاز اول:
     * - اگر pinning/advanced ندارید: یا default TLS یا null
     * - اگر قبلاً سازوکار pinning دارید، همینجا می‌توانید اعمالش کنید.
     */
    fun sslContextOrNull(): SSLContext? {
        // اگر در کد فعلی‌ات SSLContext می‌سازی، همان را برگردان
        // در غیر این صورت، null تا HiveMqttClientImpl از default TLS استفاده کند.
        return null
    }
}
