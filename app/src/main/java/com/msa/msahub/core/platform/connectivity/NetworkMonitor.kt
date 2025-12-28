package com.msa.msahub.core.platform.connectivity

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val networkState: Flow<NetworkState>
}
