package com.msa.msahub.core.platform.network.mqtt

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

enum class Qos(val value: Int) {
    AtMostOnce(0),
    AtLeastOnce(1),
    ExactlyOnce(2)
}

data class MqttMessage(
    val topic: String,
    val payload: ByteArray,
    val qos: Qos = Qos.AtLeastOnce,
    val retained: Boolean = false
)

sealed interface MqttConnectionState {
    data object Disconnected : MqttConnectionState
    data object Connecting : MqttConnectionState
    data object Connected : MqttConnectionState
    data class Error(val message: String) : MqttConnectionState
}

interface MqttClient {
    val connectionState: StateFlow<MqttConnectionState>
    val incomingMessages: SharedFlow<MqttMessage>

    suspend fun connect(config: MqttConfig)
    suspend fun disconnect()

    suspend fun subscribe(topic: String, qos: Qos = Qos.AtLeastOnce)
    suspend fun unsubscribe(topic: String)

    suspend fun publish(topic: String, payload: ByteArray, qos: Qos = Qos.AtLeastOnce, retained: Boolean = false)
}
