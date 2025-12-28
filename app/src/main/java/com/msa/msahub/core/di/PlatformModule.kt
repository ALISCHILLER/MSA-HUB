package com.msa.msahub.core.di

import com.msa.msahub.core.platform.connectivity.AndroidNetworkMonitor
import com.msa.msahub.core.platform.connectivity.NetworkMonitor
import org.koin.dsl.module

val platformModule = module {
    single<NetworkMonitor> { AndroidNetworkMonitor() }
}
