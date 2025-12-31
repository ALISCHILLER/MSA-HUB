package com.msa.msahub.features.devices.data.remote.mqtt

import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttMessage
import com.msa.msahub.core.platform.network.mqtt.Qos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class DeviceMqttHandler(
    private val mqttClient: MqttClient
) {
    suspend fun subscribeToState(deviceId: String) {
        mqttClient.subscribe(DeviceMqttTopics.statusTopic(deviceId), qos = Qos.AtLeastOnce)
    }

    fun observeState(deviceId: String): Flow<DeviceMqttEvent> {
        val topic = DeviceMqttTopics.statusTopic(deviceId)
        return mqttClient.incomingMessages
            .filter { it.topic == topic }
            .map { message ->
                DeviceStatusEvent(
                    deviceId = deviceId,
                    online = true,
                    state = emptyMap(),
                    timestamp = System.currentTimeMillis()
                )
            }
    }

    suspend fun publishCommand(topic: String, payload: ByteArray, qos: Qos, retained: Boolean) {
        mqttClient.publish(
            MqttMessage(
                topic = topic,
                payload = payload,
                qos = qos,
                retained = retained
            )
        )
    }
}
