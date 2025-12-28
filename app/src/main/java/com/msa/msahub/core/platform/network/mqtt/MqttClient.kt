package com.msa.msahub.core.platform.network.mqtt

import kotlinx.coroutines.flow.Flow

interface MqttClient {
    fun connectionState(): Flow<MqttConnectionState>
    suspend fun connect(config: MqttConfig)
    suspend fun disconnect()
    suspend fun publish(message: MqttMessage)
    suspend fun subscribe(topic: String, qos: Qos): Flow<MqttMessage>
}
