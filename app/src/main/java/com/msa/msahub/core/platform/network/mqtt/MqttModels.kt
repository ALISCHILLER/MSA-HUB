package com.msa.msahub.core.platform.network.mqtt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.net.ssl.SSLContext

/**
 * مرجع واحد تمام مدل‌ها و اینترفیس ارتباطی MQTT
 */

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
    val sslContext: SSLContext? = null
)

data class MqttMessage(
    val topic: String,
    val payload: ByteArray,
    val qos: Qos = Qos.AtLeastOnce,
    val retained: Boolean = false,
    val correlationId: String? = null // برای رهگیری فرمان‌ها
)

sealed interface MqttConnectionState {
    data object Disconnected : MqttConnectionState
    data object Connecting : MqttConnectionState
    data object Connected : MqttConnectionState
    data class Error(val message: String, val cause: Throwable? = null) : MqttConnectionState
}

interface MqttClient {
    /**
     * وضعیت لحظه‌ای اتصال به بروکر
     */
    val connectionState: StateFlow<MqttConnectionState>

    /**
     * جریان پیام‌های دریافتی از تمام Topicهای Subscribe شده
     */
    val incomingMessages: Flow<MqttMessage>

    suspend fun connect(config: MqttConfig)
    suspend fun disconnect()

    suspend fun subscribe(topic: String, qos: Qos = Qos.AtLeastOnce)
    suspend fun unsubscribe(topic: String)

    suspend fun publish(message: MqttMessage)
}
