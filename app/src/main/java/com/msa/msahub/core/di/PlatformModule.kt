package com.msa.msahub.core.di

import com.msa.msahub.core.platform.connectivity.AndroidNetworkMonitor
import com.msa.msahub.core.platform.connectivity.NetworkMonitor
import org.koin.dsl.module

object PlatformModule {
    val module = module {
        single<NetworkMonitor> { AndroidNetworkMonitor() }
    }
}
