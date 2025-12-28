package com.msa.msahub.features.devices.data.remote.mqtt

object DeviceMqttTopicParser {

    /**
     * Supported:
     *  devices/{deviceId}/state
     *  devices/{deviceId}/ack
     */
    fun parse(topic: String, payloadJson: String): DeviceMqttEvent? {
        val parts = topic.split("/")
        if (parts.size != 3) return null
        if (parts[0] != "devices") return null

        val deviceId = parts[1]
        return when (parts[2]) {
            "state" -> DeviceMqttEvent.State(deviceId, payloadJson)
            "ack" -> DeviceMqttEvent.Ack(deviceId, payloadJson)
            else -> null
        }
    }
}
