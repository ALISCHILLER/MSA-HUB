package com.msa.msahub.core.platform.settings

import com.msa.msahub.core.platform.network.http.NetworkConfig
import com.msa.msahub.core.platform.network.mqtt.MqttConfig
import com.msa.msahub.core.platform.network.mqtt.TlsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.net.ssl.SSLContext

/**
 * مدیریت پیکربندی‌های زمان اجرا (Runtime) با قابلیت به‌روزرسانی آنی از تنظیمات کاربر
 */
class RuntimeConfig(
    private val settings: SettingsDataStore,
    private val tlsProvider: TlsProvider,
    private val appScope: CoroutineScope
) {
    private val _baseUrl = MutableStateFlow("https://api.msahub.com")
    val baseUrl: StateFlow<String> = _baseUrl

    private val _mqttSettings = MutableStateFlow(MqttSettings())
    val mqttSettings: StateFlow<MqttSettings> = _mqttSettings

    init {
        // گوش دادن به تغییرات تنظیمات در سراسر برنامه
        appScope.launch {
            settings.apiBaseUrlFlow.distinctUntilChanged().collect { _baseUrl.value = it }
        }
        appScope.launch {
            settings.mqttSettingsFlow.distinctUntilChanged().collect { _mqttSettings.value = it }
        }
    }

    fun networkConfig(): NetworkConfig = NetworkConfig(baseUrl = baseUrl.value)

    fun mqttConfig(): MqttConfig {
        val s = mqttSettings.value
        val ssl: SSLContext? = tlsProvider.buildSslContext(s)

        return MqttConfig(
            host = s.host,
            port = s.port,
            clientId = "${s.clientId}_${System.currentTimeMillis()}",
            username = s.username,
            password = s.password,
            useTls = s.useTls,
            keepAlive = s.keepAliveSeconds,
            cleanStart = true,
            sslContext = ssl
        )
    }
}
