package com.msa.msahub.core.platform.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost, WiFi, Cellular
    }
}

val ConnectivityObserver.Status.isConnected: Boolean
    get() = this == ConnectivityObserver.Status.Available || 
           this == ConnectivityObserver.Status.WiFi || 
           this == ConnectivityObserver.Status.Cellular

class NetworkConnectivityObserver(context: Context) : ConnectivityObserver {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observe(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    val caps = connectivityManager.getNetworkCapabilities(network)
                    val status = when {
                        caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> ConnectivityObserver.Status.WiFi
                        caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> ConnectivityObserver.Status.Cellular
                        else -> ConnectivityObserver.Status.Available
                    }
                    launch { send(status) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    launch { send(ConnectivityObserver.Status.Losing) }
                }

                override fun onLost(network: Network) {
                    launch { send(ConnectivityObserver.Status.Lost) }
                }

                override fun onUnavailable() {
                    launch { send(ConnectivityObserver.Status.Unavailable) }
                }
            }

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, callback)
            
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}
