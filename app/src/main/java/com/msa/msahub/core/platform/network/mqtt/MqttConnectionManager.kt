package com.msa.msahub.core.platform.network.mqtt

import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.ConnectivityObserver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min
import kotlin.math.pow

/**
 * مدیریت چرخه حیات اتصال MQTT با رعایت پایداری، Exponential Backoff و مدیریت وضعیت شبکه.
 */
class MqttConnectionManager(
    private val mqttClient: MqttClient,
    private val configProvider: () -> MqttConfig,
    private val connectivityObserver: ConnectivityObserver,
    private val scope: CoroutineScope,
    private val logger: Logger
) {
    private val connectMutex = Mutex()
    private val reconnectAttempt = AtomicInteger(0)
    private var connectionJob: Job? = null

    /**
     * شروع نظارت بر شبکه و مدیریت خودکار اتصال.
     */
    fun start() {
        scope.launch {
            connectivityObserver.observe()
                .distinctUntilChanged()
                .collectLatest { status ->
                    when (status) {
                        ConnectivityObserver.Status.Available -> {
                            logger.i("Network available. Starting connection logic.")
                            resetBackoff()
                            startConnectionLoop()
                        }
                        else -> {
                            logger.w("Network lost ($status). Suspending MQTT connection.")
                            stopConnectionLoop()
                            mqttClient.disconnect()
                        }
                    }
                }
        }
    }

    private fun startConnectionLoop() {
        connectionJob?.cancel()
        connectionJob = scope.launch {
            while (isActive) {
                val currentState = mqttClient.connectionState.value
                if (currentState is MqttConnectionState.Connected) {
                    // اگر متصل هستیم، فقط منتظر می‌مانیم تا وضعیت تغییر کند
                    mqttClient.connectionState.first { it !is MqttConnectionState.Connected }
                    continue
                }

                connectWithRetry()
            }
        }
    }

    private fun stopConnectionLoop() {
        connectionJob?.cancel()
        connectionJob = null
    }

    private suspend fun connectWithRetry() = connectMutex.withLock {
        val config = configProvider()
        val attempt = reconnectAttempt.getAndIncrement()
        
        if (attempt > 0) {
            val delayMs = calculateBackoff(attempt, config.initialReconnectDelayMs, config.maxReconnectDelayMs)
            logger.d("Retrying MQTT connection in ${delayMs}ms (attempt $attempt)")
            delay(delayMs)
        }

        try {
            logger.i("Attempting to connect to MQTT broker: ${config.host}:${config.port}")
            mqttClient.connect(config)
            // اگر با موفقیت متصل شد، در متد startConnectionLoop وضعیت تغییر کرده و حلقه ادامه می‌یابد
            resetBackoff()
        } catch (e: Exception) {
            logger.e("MQTT connection attempt $attempt failed", e)
            if (e is CancellationException) throw e
        }
    }

    private fun resetBackoff() {
        reconnectAttempt.set(0)
    }

    private fun calculateBackoff(attempt: Int, initialDelay: Long, maxDelay: Long): Long {
        val exp = 2.0.pow(attempt.coerceAtMost(10).toDouble()).toLong()
        return min(initialDelay * exp, maxDelay)
    }
}
