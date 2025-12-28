package com.msa.msahub.features.devices.domain.model

sealed interface CommandAck {
    data object Success : CommandAck
    data class Failure(val reason: String) : CommandAck
    data object QueuedOffline : CommandAck
}
