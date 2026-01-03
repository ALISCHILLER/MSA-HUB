package com.msa.msahub.core.platform.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AndroidNetworkMonitor(
    context: Context
) : NetworkMonitor {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _state = MutableStateFlow(currentState())
    override val state: StateFlow<NetworkState> = _state

    private var registered = false

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _state.value = currentState()
        }

        override fun onLost(network: Network) {
            _state.value = currentState()
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            _state.value = computeState(networkCapabilities)
        }
    }

    override fun start() {
        if (registered) return
        registered = true
        val req = NetworkRequest.Builder().build()
        cm.registerNetworkCallback(req, callback)
        _state.value = currentState()
    }

    override fun stop() {
        if (!registered) return
        registered = false
        runCatching { cm.unregisterNetworkCallback(callback) }
    }

    private fun currentState(): NetworkState {
        val active = cm.activeNetwork ?: return NetworkState.Disconnected
        val caps = cm.getNetworkCapabilities(active) ?: return NetworkState.Disconnected
        return computeState(caps)
    }

    private fun computeState(caps: NetworkCapabilities): NetworkState {
        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val validated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return when {
            !hasInternet -> NetworkState.Disconnected
            validated -> NetworkState.Connected
            else -> NetworkState.Degraded // اینترنت هست ولی هنوز validate نشده (مثلاً captive portal)
        }
    }
}
