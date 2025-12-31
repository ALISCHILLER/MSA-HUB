package com.msa.msahub.features.devices.domain.model

sealed interface CommandAck {
    data object Success : CommandAck
    data object QueuedOffline : CommandAck
}
