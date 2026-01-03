package com.msa.msahub.core.platform.connectivity

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
    val state: StateFlow<NetworkState>

    fun start()
    fun stop()
}
