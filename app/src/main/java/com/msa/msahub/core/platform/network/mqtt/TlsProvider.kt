package com.msa.msahub.core.platform.network.mqtt

import android.content.Context
import com.msa.msahub.core.platform.settings.MqttSettings
import java.io.BufferedInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class TlsProvider(private val context: Context) {

    fun buildSslContext(settings: MqttSettings): SSLContext? {
        if (!settings.useTls) return null
        if (!settings.enablePinning) return null 

        val resId = context.resources.getIdentifier(
            settings.pinnedCertAssetName,
            "raw",
            context.packageName
        )
        if (resId == 0) {
            return null
        }

        return runCatching {
            val cf = CertificateFactory.getInstance("X.509")
            val ca = context.resources.openRawResource(resId).use { input ->
                BufferedInputStream(input).use { bis ->
                    cf.generateCertificate(bis)
                }
            }

            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType).apply {
                load(null, null)
                setCertificateEntry("ca", ca)
            }

            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
                init(keyStore)
            }

            SSLContext.getInstance("TLS").apply {
                init(null, tmf.trustManagers, null)
            }
        }.getOrNull()
    }
}
