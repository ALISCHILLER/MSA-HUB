package com.msa.msahub.core.platform.network.mqtt.impl

import com.hivemq.client.mqtt.MqttClient as HiveMqtt
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import com.msa.msahub.core.platform.network.mqtt.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.await
import timber.log.Timber
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class HiveMqttClientImpl : MqttClient {

    private var client: Mqtt5AsyncClient? = null
    
    private val _connectionState = MutableStateFlow<MqttConnectionState>(MqttConnectionState.Disconnected)
    override val connectionState: StateFlow<MqttConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<MqttMessage>(
        replay = 0,
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val incomingMessages: Flow<MqttMessage> = _incomingMessages.asSharedFlow()

    // نگهداری لیست اشتراک‌ها برای Re-subscribe خودکار
    private val activeSubscriptions = ConcurrentHashMap<String, Qos>()

    override suspend fun connect(config: MqttConfig) {
        if (_connectionState.value == MqttConnectionState.Connected) return

        _connectionState.value = MqttConnectionState.Connecting

        try {
            val builder = HiveMqtt.builder()
                .useMqttVersion5()
                .identifier(config.clientId)
                .serverHost(config.host)
                .serverPort(config.port)
                .automaticReconnectWithDefaultConfig() // فعال‌سازی Reconnect خودکار HiveMQ

            if (config.username != null) {
                builder.simpleAuth()
                    .username(config.username)
                    .password(config.password?.toByteArray())
                    .applySimpleAuth()
            }

            val asyncClient = builder.buildAsync()
            client = asyncClient

            // گوش دادن به تغییرات وضعیت اتصال در سطح کلاینت
            asyncClient.toAsync().subscribeWith()
                .callback { publish -> handleIncomingPublish(publish) }
                .send()

            val connAck: Mqtt5ConnAck = asyncClient.connectWith()
                .cleanStart(config.cleanStart)
                .keepAlive(config.keepAlive)
                .send()
                .await()

            _connectionState.value = MqttConnectionState.Connected
            Timber.i("MQTT Connected: ${connAck.reasonCode}")

            // Re-subscribe to existing topics if any
            reSubscribeAll()

        } catch (e: Exception) {
            Timber.e(e, "MQTT Connection Failed")
            _connectionState.value = MqttConnectionState.Error("Connection failed: ${e.message}", e)
        }
    }

    override suspend fun disconnect() {
        client?.disconnect()?.await()
        _connectionState.value = MqttConnectionState.Disconnected
    }

    override suspend fun subscribe(topic: String, qos: Qos) {
        activeSubscriptions[topic] = qos
        client?.subscribeWith()
            .topicFilter(topic)
            .qos(mapQos(qos))
            .send()
            ?.await()
    }

    override suspend fun unsubscribe(topic: String) {
        activeSubscriptions.remove(topic)
        client?.unsubscribeWith()
            .topicFilter(topic)
            .send()
            ?.await()
    }

    override suspend fun publish(message: MqttMessage) {
        val publishBuilder = client?.publishWith()
            ?.topic(message.topic)
            ?.payload(message.payload)
            ?.qos(mapQos(message.qos))
            ?.retain(message.retained)

        message.correlationId?.let {
            publishBuilder?.correlationData(it.toByteArray())
        }

        publishBuilder?.send()?.await()
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
        activeSubscriptions.forEach { (topic, qos) ->
            subscribe(topic, qos)
        }
    }

    private fun mapQos(qos: Qos): MqttQos = when (qos) {
        Qos.AtMostOnce -> MqttQos.AT_MOST_ONCE
        Qos.AtLeastOnce -> MqttQos.AT_LEAST_ONCE
        Qos.ExactlyOnce -> MqttQos.EXACTLY_ONCE
    }

    private fun mapFromHiveQos(qos: MqttQos): Qos = when (qos) {
        MqttQos.AT_MOST_ONCE -> Qos.AtMostOnce
        MqttQos.AT_LEAST_ONCE -> Qos.AtLeastOnce
        MqttQos.EXACTLY_ONCE -> Qos.ExactlyOnce
    }
}
