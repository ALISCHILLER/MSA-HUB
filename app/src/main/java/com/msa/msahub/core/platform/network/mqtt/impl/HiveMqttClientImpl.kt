package com.msa.msahub.core.platform.network.mqtt.impl

import com.hivemq.client.mqtt.MqttClient as HiveMqtt
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import com.hivemq.client.mqtt.mqtt5.Mqtt5GlobalPublishFilter
import com.msa.msahub.core.platform.network.mqtt.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.await
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

class HiveMqttClientImpl : MqttClient {

    private var client: Mqtt5AsyncClient? = null

    private val _connectionState = MutableStateFlow<MqttConnectionState>(MqttConnectionState.Disconnected)
    override val connectionState: StateFlow<MqttConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<MqttMessage>(
        replay = 0,
        extraBufferCapacity = 200,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val incomingMessages: Flow<MqttMessage> = _incomingMessages.asSharedFlow()

    private val activeSubscriptions = ConcurrentHashMap<String, Qos>()

    override suspend fun connect(config: MqttConfig) {
        if (_connectionState.value == MqttConnectionState.Connected ||
            _connectionState.value == MqttConnectionState.Connecting
        ) return

        _connectionState.value = MqttConnectionState.Connecting

        try {
            val builder = HiveMqtt.builder()
                .useMqttVersion5()
                .identifier(config.clientId)
                .serverHost(config.host)
                .serverPort(config.port)
                .automaticReconnectWithDefaultConfig()

            if (config.useTls) {
                val ssl = config.sslContext
                if (ssl != null) {
                    builder.sslConfig().sslContext(ssl).applySslConfig()
                } else {
                    builder.sslWithDefaultConfig()
                }
            }

            if (config.username != null) {
                builder.simpleAuth()
                    .username(config.username)
                    .password(config.password?.toByteArray())
                    .applySimpleAuth()
            }

            val asyncClient = builder.buildAsync()
            client = asyncClient

            // استفاده از Global Publish Listener برای پایداری دریافت پیام
            asyncClient.publishes(Mqtt5GlobalPublishFilter.ALL) { publish ->
                handleIncomingPublish(publish)
            }

            val connAck: Mqtt5ConnAck = asyncClient.connectWith()
                .cleanStart(config.cleanStart)
                .keepAlive(config.keepAlive)
                .send()
                .await()

            _connectionState.value = MqttConnectionState.Connected
            Timber.i("MQTT Connected: ${connAck.reasonCode}")

            reSubscribeAll()
        } catch (e: Exception) {
            Timber.e(e, "MQTT Connection Failed")
            _connectionState.value = MqttConnectionState.Error("Connection failed: ${e.message}", e)
        }
    }

    override suspend fun disconnect() {
        runCatching { client?.disconnect()?.await() }
        client = null
        _connectionState.value = MqttConnectionState.Disconnected
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
        client?.publishWith()
            ?.topic(message.topic)
            ?.payload(message.payload)
            ?.qos(mapQos(message.qos))
            ?.retain(message.retained)
            ?.apply {
                message.correlationId?.let { correlationData(it.toByteArray()) }
            }
            ?.send()
            ?.await()
    }

    private fun handleIncomingPublish(publish: Mqtt5Publish) {
        val topic = publish.topic.toString()
        val payload = publish.payloadAsBytes
        val correlationId = publish.correlationData.map { String(it.array()) }.orElse(null)

        _incomingMessages.tryEmit(
            MqttMessage(
                topic = topic,
                payload = payload,
                qos = mapFromHiveQos(publish.qos),
                retained = publish.isRetain,
                correlationId = correlationId
            )
        )
    }

    private suspend fun reSubscribeAll() {
        activeSubscriptions.forEach { (topic, qos) -> subscribe(topic, qos) }
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
