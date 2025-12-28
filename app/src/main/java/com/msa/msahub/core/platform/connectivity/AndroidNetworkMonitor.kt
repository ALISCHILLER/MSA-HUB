package com.msa.msahub.core.platform.connectivity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidNetworkMonitor : NetworkMonitor {
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Disconnected)
    override val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()
}
