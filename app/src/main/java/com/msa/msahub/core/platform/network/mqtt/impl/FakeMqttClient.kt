package com.msa.msahub.core.platform.network.mqtt.impl

import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttConfig
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionState
import com.msa.msahub.core.platform.network.mqtt.MqttMessage
import com.msa.msahub.core.platform.network.mqtt.Qos
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeMqttClient : MqttClient {

    private val _connectionState = MutableStateFlow<MqttConnectionState>(MqttConnectionState.Disconnected)
    override val connectionState = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<MqttMessage>(extraBufferCapacity = 64)
    override val incomingMessages = _incomingMessages.asSharedFlow()

    override suspend fun connect(config: MqttConfig) {
        _connectionState.value = MqttConnectionState.Connecting
        _connectionState.value = MqttConnectionState.Connected
    }

    override suspend fun disconnect() {
        _connectionState.value = MqttConnectionState.Disconnected
    }

    override suspend fun subscribe(topic: String, qos: Qos) {
        // no-op
    }

    override suspend fun unsubscribe(topic: String) {
        // no-op
    }

    override suspend fun publish(message: MqttMessage) {
        _incomingMessages.tryEmit(message)
    }
}
