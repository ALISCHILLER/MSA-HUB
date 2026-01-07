package com.msa.msahub.core.platform.network.mqtt

import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.config.MqttRuntimeConfig
import com.msa.msahub.core.platform.network.ConnectivityObserver
import com.msa.msahub.core.platform.network.isConnected
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.pow

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
    
    private var retryAttempt = 0
    private val baseDelayMs = 2000L
    private val maxDelayMs = 60000L

    fun start() {
        if (started) return
        started = true

        logger.i("[MQTT] Manager starting...")

        scope.launch {
            connectivityObserver.observe().collect { state ->
                logger.d("[MQTT] Network state changed: $state")
                if (state.isConnected) {
                    logger.i("[MQTT] Internet restored. Resetting backoff and connecting.")
                    resetBackoff()
                    ensureConnected(reason = "network_connected")
                }
            }
        }

        configJob?.cancel()
        configJob = runtimeConfigProvider.config
            .debounce(500)
            .onEach { cfg ->
                if (lastApplied != null && !equivalent(lastApplied!!, cfg)) {
                    logger.i("[MQTT] Configuration updated by user. Reconnecting...")
                    resetBackoff()
                    reconnect(reason = "config_changed")
                }
                lastApplied = cfg
            }
            .launchIn(scope)
            
        ensureConnected(reason = "startup")
    }

    private fun ensureConnected(reason: String) {
        scope.launch {
            if (mqttClient.connectionState.value == MqttConnectionState.Connected) {
                logger.d("[MQTT] Already connected. Skipping ensureConnected ($reason).")
                return@launch
            }
            connectInternal(reason)
        }
    }

    private suspend fun connectInternal(reason: String) {
        reconnectMutex.withLock {
            val cfg = buildMqttConfig(runtimeConfigProvider.current())
            logger.i("[MQTT] Attempting connection ($reason) to ${cfg.host}:${cfg.port} as ${cfg.clientId}")
            
            runCatching {
                mqttClient.connect(cfg)
                logger.i("[MQTT] Successfully connected to broker.")
                resetBackoff()
            }.onFailure { e ->
                logger.e("[MQTT] Connection attempt failed: ${e.message}", e)
                scheduleRetry(reason)
            }
        }
    }

    private fun scheduleRetry(reason: String) {
        val delay = calculateBackoff()
        logger.w("[MQTT] Scheduling retry in ${delay}ms (Attempt #$retryAttempt) due to $reason")
        
        scope.launch {
            delay(delay)
            if (mqttClient.connectionState.value != MqttConnectionState.Connected) {
                retryAttempt++
                connectInternal("retry_$reason")
            }
        }
    }

    private fun calculateBackoff(): Long {
        val exp = 2.0.pow(retryAttempt.toDouble()).toLong()
        return (baseDelayMs * exp).coerceAtMost(maxDelayMs)
    }

    private fun resetBackoff() {
        if (retryAttempt > 0) logger.d("[MQTT] Resetting retry backoff.")
        retryAttempt = 0
    }

    private fun reconnect(reason: String) {
        scope.launch {
            logger.i("[MQTT] Manual reconnection triggered: $reason")
            runCatching { mqttClient.disconnect() }
            connectInternal(reason)
        }
    }

    private fun buildMqttConfig(m: MqttRuntimeConfig): MqttConfig {
        val ssl = if (m.useTls) tlsProvider.sslContextOrNull() else null
        return MqttConfig(
            host = m.host,
            port = m.port,
            clientId = m.clientIdPrefix,
            username = m.username,
            password = m.password,
            useTls = m.useTls,
            keepAlive = m.keepAliveSec,
            cleanStart = false,
            sslContext = ssl
        )
    }

    private fun equivalent(a: MqttRuntimeConfig, b: MqttRuntimeConfig): Boolean {
        return a.host == b.host && a.port == b.port && a.useTls == b.useTls &&
               a.username == b.username && a.password == b.password
    }
}
