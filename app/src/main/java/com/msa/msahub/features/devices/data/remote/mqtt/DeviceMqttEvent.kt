package com.msa.msahub.features.devices.data.remote.mqtt

sealed interface DeviceMqttEvent {
    data class StateUpdated(val deviceId: String, val payload: ByteArray) : DeviceMqttEvent
}
