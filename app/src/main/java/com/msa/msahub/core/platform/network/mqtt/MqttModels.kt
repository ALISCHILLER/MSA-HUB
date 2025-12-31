package com.msa.msahub.core.platform.network.mqtt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.net.ssl.SSLContext

enum class Qos(val value: Int) {
    AtMostOnce(0),
    AtLeastOnce(1),
    ExactlyOnce(2)
}

data class MqttConfig(
    val host: String,
    val port: Int,
    val clientId: String,
    val username: String? = null,
    val password: String? = null,
    val useTls: Boolean = false,
    val cleanStart: Boolean = true,
    val keepAlive: Int = 60,
    val sslContext: SSLContext? = null,
    val maxReconnectDelayMs: Long = 60_000L,
    val initialReconnectDelayMs: Long = 1_000L
)

data class MqttMessage(
    val topic: String,
    val payload: ByteArray,
    val qos: Qos = Qos.AtLeastOnce,
    val retained: Boolean = false,
    val correlationId: String? = null
)

sealed interface MqttConnectionState {
    data object Disconnected : MqttConnectionState
    data object Connecting : MqttConnectionState
    data object Connected : MqttConnectionState
    data object Suspended : MqttConnectionState
    data class Failed(val message: String, val cause: Throwable? = null) : MqttConnectionState
}

interface MqttClient {
    val connectionState: StateFlow<MqttConnectionState>
    val incomingMessages: Flow<MqttMessage>

    suspend fun connect(config: MqttConfig)
    suspend fun disconnect()
    suspend fun subscribe(topic: String, qos: Qos = Qos.AtLeastOnce)
    suspend fun unsubscribe(topic: String)
    suspend fun publish(message: MqttMessage)
}
