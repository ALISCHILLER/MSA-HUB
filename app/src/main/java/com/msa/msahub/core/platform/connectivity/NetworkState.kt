package com.msa.msahub.core.platform.connectivity

sealed class NetworkState {
    object Connected : NetworkState()
    object Disconnected : NetworkState()
    data class Degraded(val quality: ConnectionQuality) : NetworkState()
}
