package com.msa.msahub.core.platform.network.mqtt

import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.ConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * مدیریت چرخه حیات اتصال MQTT با رعایت پایداری و بهینه‌سازی منابع
 */
class MqttConnectionManager(
    private val mqttClient: MqttClient,
    private val configProvider: () -> MqttConfig,
    private val connectivityObserver: ConnectivityObserver,
    private val scope: CoroutineScope,
    private val logger: Logger
) {
    private val connectMutex = Mutex()

    fun start() {
        scope.launch {
            connectivityObserver.observe()
                .distinctUntilChanged()
                .collectLatest { status ->
                    if (status == ConnectivityObserver.Status.Available) {
                        logger.i("Network available, attempting MQTT connection...")
                        
                        // وقفه کوتاه برای اطمینان از پایداری شبکه قبل از تلاش برای اتصال
                        delay(500)

                        connectMutex.withLock {
                            val state = mqttClient.connectionState.value
                            if (state is MqttConnectionState.Connected || state is MqttConnectionState.Connecting) {
                                return@withLock
                            }
                            
                            runCatching { 
                                mqttClient.connect(configProvider()) 
                            }.onFailure { 
                                logger.e("MQTT connection failed", it) 
                            }
                        }
                    } else {
                        logger.w("Network lost, MQTT will disconnect")
                        runCatching { mqttClient.disconnect() }
                    }
                }
        }
    }
}
