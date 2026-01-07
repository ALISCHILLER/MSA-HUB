package com.msa.msahub.core.platform.network.mqtt.impl

import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttConfig
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionState
import com.msa.msahub.core.platform.network.mqtt.MqttMessage
import com.msa.msahub.core.platform.network.mqtt.Qos
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

class HiveMqttClientImpl : MqttClient {

    private val clientMutex = Mutex()
    private var client: Mqtt5AsyncClient? = null

    private val _connectionState = MutableStateFlow<MqttConnectionState>(MqttConnectionState.Disconnected)
    override val connectionState: StateFlow<MqttConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<MqttMessage>(
        replay = 0,
        extraBufferCapacity = 500,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val incomingMessages: Flow<MqttMessage> = _incomingMessages.asSharedFlow()

    private val activeSubscriptions = ConcurrentHashMap<String, Qos>()

    override suspend fun connect(config: MqttConfig) = clientMutex.withLock {
        if (_connectionState.value is MqttConnectionState.Connected) return@withLock
        
        _connectionState.value = MqttConnectionState.Connecting
        
        try {
            val builder = Mqtt5Client.builder()
                .identifier(config.clientId)
                .serverHost(config.host)
                .serverPort(config.port)
                .automaticReconnectWithDefaultConfig()

            if (config.useTls) {
                if (config.sslContext != null) {
                    builder.sslConfig()
                        .sslContext(config.sslContext)
                        .applySslConfig()
                } else {
                    builder.sslWithDefaultConfig()
                }
            }

            config.username?.let { username ->
                val authBuilder = builder.simpleAuth().username(username)
                config.password?.let { authBuilder.password(it.toByteArray()) }
                authBuilder.applySimpleAuth()
            }

            val asyncClient = builder.buildAsync()
            client = asyncClient

            asyncClient.publishes(MqttGlobalPublishFilter.ALL) { publish ->
                handleIncomingPublish(publish)
            }

            val connAck: Mqtt5ConnAck = asyncClient.connectWith()
                .cleanStart(config.cleanStart)
                .keepAlive(config.keepAlive)
                .send()
                .await()

            if (connAck.reasonCode.isError) {
                throw IllegalStateException("MQTT Connect Error: ${connAck.reasonCode}")
            }

            _connectionState.value = MqttConnectionState.Connected
            Timber.i("MQTT Connected Successfully as ${config.clientId}")

            reSubscribeAll()

        } catch (e: Exception) {
            Timber.e(e, "MQTT Connection Failed")
            _connectionState.value = MqttConnectionState.Failed("Connection failed: ${e.message}", e)
            client = null
            throw e
        }
    }

    override suspend fun disconnect() {
        clientMutex.withLock {
            try {
                client?.disconnect()?.await()
            } finally {
                client = null
                _connectionState.value = MqttConnectionState.Disconnected
            }
        }
    }

    override suspend fun subscribe(topic: String, qos: Qos) {
        activeSubscriptions[topic] = qos
        client?.subscribeWith()
            ?.topicFilter(topic)
            ?.qos(mapQos(qos))
            ?.send()
            ?.await()
    }

    override suspend fun unsubscribe(topic: String) {
        activeSubscriptions.remove(topic)
        client?.unsubscribeWith()
            ?.topicFilter(topic)
            ?.send()
            ?.await()
    }

    override suspend fun publish(message: MqttMessage) {
        val currentClient = client ?: throw IllegalStateException("MQTT Client not connected")
        
        currentClient.publishWith()
            .topic(message.topic)
            .payload(message.payload)
            .qos(mapQos(message.qos))
            .retain(message.retained)
            .apply {
                message.correlationId?.let { correlationData(it.toByteArray()) }
            }
            .send()
            .await()
    }

    private fun handleIncomingPublish(publish: Mqtt5Publish) {
        val topic = publish.topic.toString()
        val payload = publish.payloadAsBytes
        val correlationId = publish.correlationData.map { String(it.array()) }.orElse(null)

        val message = MqttMessage(
            topic = topic,
            payload = payload,
            qos = mapFromHiveQos(publish.qos),
            retained = publish.isRetain,
            correlationId = correlationId
        )
        
        _incomingMessages.tryEmit(message)
    }

    private suspend fun reSubscribeAll() {
        activeSubscriptions.forEach { (topic, qos) ->
            runCatching { subscribe(topic, qos) }
        }
    }

    private fun mapQos(qos: Qos): com.hivemq.client.mqtt.datatypes.MqttQos = when (qos) {
        Qos.AtMostOnce -> com.hivemq.client.mqtt.datatypes.MqttQos.AT_MOST_ONCE
        Qos.AtLeastOnce -> com.hivemq.client.mqtt.datatypes.MqttQos.AT_LEAST_ONCE
        Qos.ExactlyOnce -> com.hivemq.client.mqtt.datatypes.MqttQos.EXACTLY_ONCE
    }

    private fun mapFromHiveQos(qos: com.hivemq.client.mqtt.datatypes.MqttQos): Qos = when (qos) {
        com.hivemq.client.mqtt.datatypes.MqttQos.AT_MOST_ONCE -> Qos.AtMostOnce
        com.hivemq.client.mqtt.datatypes.MqttQos.AT_LEAST_ONCE -> Qos.AtLeastOnce
        com.hivemq.client.mqtt.datatypes.MqttQos.EXACTLY_ONCE -> Qos.ExactlyOnce
    }
}
