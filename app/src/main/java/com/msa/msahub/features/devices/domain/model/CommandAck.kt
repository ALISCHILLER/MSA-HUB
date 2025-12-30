package com.msa.msahub.features.devices.domain.model

sealed interface CommandAck {
    val commandId: String

    data class QueuedOffline(override val commandId: String) : CommandAck
    data class Sent(override val commandId: String) : CommandAck
    data class Acked(override val commandId: String) : CommandAck
    data class Failure(override val commandId: String, val message: String?) : CommandAck
}
