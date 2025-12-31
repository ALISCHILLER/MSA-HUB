package com.msa.msahub.features.devices.data.remote.mqtt

object DeviceMqttTopics {
    fun statusTopic(deviceId: String): String = "devices/$deviceId/status"
    fun commandTopic(deviceId: String): String = "devices/$deviceId/command"
    fun ackTopic(deviceId: String): String = "devices/$deviceId/ack"
    
    const val ALL_DEVICES_STATUS = "devices/+/status"
    const val ALL_DEVICES_ACK = "devices/+/ack"

    fun extractDeviceId(topic: String): String? {
        val parts = topic.split("/")
        return if (parts.size >= 2 && parts[0] == "devices") parts[1] else null
    }
}
