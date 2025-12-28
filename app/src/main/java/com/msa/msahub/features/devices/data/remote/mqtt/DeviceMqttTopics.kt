package com.msa.msahub.features.devices.data.remote.mqtt

object DeviceMqttTopics {
    // Subscribe topics
    const val ALL_DEVICES_STATUS = "devices/+/status"
    const val ALL_DEVICES_ACK = "devices/+/ack"
    
    // Publish topics (for reference)
    fun commandTopic(deviceId: String): String = "devices/$deviceId/command"
    fun statusTopic(deviceId: String): String = "devices/$deviceId/status"
    fun ackTopic(deviceId: String): String = "devices/$deviceId/ack"
    
    // Extract device ID from topic
    fun extractDeviceId(topic: String): String? {
        return when {
            topic.startsWith("devices/") && topic.endsWith("/status") -> {
                topic.removePrefix("devices/").removeSuffix("/status")
            }
            topic.startsWith("devices/") && topic.endsWith("/ack") -> {
                topic.removePrefix("devices/").removeSuffix("/ack")
            }
            else -> null
        }
    }
}
