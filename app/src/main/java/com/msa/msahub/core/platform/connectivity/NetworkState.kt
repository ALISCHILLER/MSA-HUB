package com.msa.msahub.core.platform.connectivity

sealed interface NetworkState {
    data object Connected : NetworkState
    data object Disconnected : NetworkState
    data class Degraded(val reason: String) : NetworkState
    
    fun isOnline(): Boolean = this is Connected || this is Degraded
}

enum class ConnectionType {
    WIFI, CELLULAR, ETHERNET, NONE
}
