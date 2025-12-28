package com.msa.msahub.features.devices.data.remote.mqtt

object DeviceMqttTopics {
    fun commandTopic(deviceId: String): String = "devices/$deviceId/command"
    fun stateTopic(deviceId: String): String = "devices/$deviceId/state"
}
