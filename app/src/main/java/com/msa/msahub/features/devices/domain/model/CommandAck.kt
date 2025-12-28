package com.msa.msahub.features.devices.domain.model

sealed interface CommandAck {
    data object Sent : CommandAck
    data object QueuedOffline : CommandAck
    data class Failure(val error: String) : CommandAck
}
