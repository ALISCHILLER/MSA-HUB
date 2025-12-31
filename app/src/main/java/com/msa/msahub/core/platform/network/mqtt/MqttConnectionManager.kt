package com.msa.msahub.core.platform.network.mqtt

import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.ConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MqttConnectionManager(
    private val mqttClient: MqttClient,
    private val config: MqttConfig,
    private val connectivityObserver: ConnectivityObserver,
    private val scope: CoroutineScope,
    private val logger: Logger
) {
    fun start() {
        scope.launch {
            connectivityObserver.observe().collectLatest { status ->
                if (status == ConnectivityObserver.Status.Available) {
                    logger.i("Network available, attempting MQTT connection...")
                    runCatching {
                        mqttClient.connect(config)
                    }.onFailure { 
                        logger.e("MQTT connection failed", it) 
                    }
                } else {
                    logger.w("Network lost, MQTT will disconnect")
                    mqttClient.disconnect()
                }
            }
        }
    }
}
