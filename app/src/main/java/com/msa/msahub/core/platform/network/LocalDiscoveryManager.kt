package com.msa.msahub.core.platform.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.msa.msahub.core.common.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalDiscoveryManager(
    private val context: Context,
    private val logger: Logger
) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val serviceType = "_mqtt._tcp."

    private val _discoveredServices = MutableStateFlow<List<NsdServiceInfo>>(emptyList())
    val discoveredServices: StateFlow<List<NsdServiceInfo>> = _discoveredServices.asStateFlow()

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(regType: String) {
            logger.d("NSD Discovery started")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            logger.d("NSD Service found: ${service.serviceName}")
            if (service.serviceType == serviceType) {
                nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                        logger.e("NSD Resolve failed: $errorCode")
                    }

                    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                        logger.i("NSD Service resolved: ${serviceInfo.host.hostAddress}:${serviceInfo.port}")
                        _discoveredServices.value = _discoveredServices.value + serviceInfo
                    }
                })
            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            logger.d("NSD Service lost: ${service.serviceName}")
            _discoveredServices.value = _discoveredServices.value.filter { it.serviceName != service.serviceName }
        }

        override fun onDiscoveryStopped(regType: String) {
            logger.d("NSD Discovery stopped")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            logger.e("NSD Start discovery failed: $errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            logger.e("NSD Stop discovery failed: $errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
    }

    fun startDiscovery() {
        runCatching {
            nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        }.onFailure { e ->
            logger.e("NSD failed to start discovery", e)
        }
    }

    fun stopDiscovery() {
        runCatching {
            nsdManager.stopServiceDiscovery(discoveryListener)
        }
    }
}
