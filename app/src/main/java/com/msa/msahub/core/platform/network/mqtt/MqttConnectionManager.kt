package com.msa.msahub.core.platform.network.mqtt

import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.config.MqttRuntimeConfig
import com.msa.msahub.core.platform.network.ConnectivityObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MqttConnectionManager(
    private val mqttClient: MqttClient,
    private val runtimeConfigProvider: MqttRuntimeConfigProvider,
    private val tlsProvider: TlsProvider,
    private val connectivityObserver: ConnectivityObserver,
    private val scope: CoroutineScope,
    private val logger: Logger
) {
    private val reconnectMutex = Mutex()
    private var configJob: Job? = null
    private var lastApplied: MqttRuntimeConfig? = null
    private var started = false

    fun start() {
        if (started) return
        started = true

        // existing logic: listen network state and try connect/reconnect
        scope.launch {
            connectivityObserver.observe().collect { state ->
                logger.i("Network state changed: $state")
                if (state.isConnected) {
                    ensureConnected(reason = "network_connected")
                } else {
                    // optional: disconnect
                }
            }
        }

        // ✅ NEW: listen to config changes and reconnect when it changes
        configJob?.cancel()
        configJob = runtimeConfigProvider.config
            .debounce(400) // جلوگیری از reconnect های پشت‌سرهم وقتی کاربر تایپ می‌کند
            .distinctUntilChangedByKey()
            .onEach { cfg ->
                val prev = lastApplied
                lastApplied = cfg
                if (prev == null) {
                    // اولین config
                    ensureConnected(reason = "initial_config")
                } else if (!equivalent(prev, cfg)) {
                    logger.i("MQTT config changed => reconnect")
                    reconnect(reason = "config_changed")
                }
            }
            .launchIn(scope)
    }

    private fun ensureConnected(reason: String) {
        scope.launch {
            val cfg = buildMqttConfig(runtimeConfigProvider.current())
            runCatching { mqttClient.connect(cfg) }
                .onFailure { e -> logger.e("MQTT connect failed ($reason)", e) }
        }
    }

    private fun reconnect(reason: String) {
        scope.launch {
            reconnectMutex.withLock {
                // در صورت نیاز کمی delay برای settle شدن شبکه/دیباونس
                delay(150)
                runCatching {
                    // اگر متد شما disconnect/close فرق دارد، همین خط را مطابق API خودت تنظیم کن
                    mqttClient.disconnect()
                }.onFailure { /* ignore */ }

                val cfg = buildMqttConfig(runtimeConfigProvider.current())
                runCatching { mqttClient.connect(cfg) }
                    .onFailure { e -> logger.e("MQTT reconnect failed ($reason)", e) }
            }
        }
    }

    private fun buildMqttConfig(m: MqttRuntimeConfig): MqttConfig {
        val ssl = if (m.useTls) tlsProvider.sslContextOrNull() else null
        return MqttConfig(
            host = m.host,
            port = m.port,
            clientId = "${m.clientIdPrefix}_${System.currentTimeMillis()}",
            username = m.username,
            password = m.password,
            useTls = m.useTls,
            keepAlive = m.keepAliveSec,
            cleanStart = true,
            sslContext = ssl
        )
    }

    private fun equivalent(a: MqttRuntimeConfig, b: MqttRuntimeConfig): Boolean {
        return a.host == b.host &&
            a.port == b.port &&
            a.useTls == b.useTls &&
            a.username == b.username &&
            a.password == b.password &&
            a.keepAliveSec == b.keepAliveSec &&
            a.clientIdPrefix == b.clientIdPrefix
    }
}

// helper برای distinctUntilChanged روی کلیدهای مهم
private fun kotlinx.coroutines.flow.Flow<MqttRuntimeConfig>.distinctUntilChangedByKey() =
    this.map { it } // no-op map برای خوانایی
        .distinctUntilChanged { old, new ->
            old.host == new.host &&
            old.port == new.port &&
            old.useTls == new.useTls &&
            old.username == new.username &&
            old.password == new.password &&
            old.keepAliveSec == new.keepAliveSec &&
            old.clientIdPrefix == new.clientIdPrefix
        }
