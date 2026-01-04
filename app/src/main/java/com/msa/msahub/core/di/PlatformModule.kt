package com.msa.msahub.core.di

import com.msa.msahub.core.platform.connectivity.AndroidNetworkMonitor
import com.msa.msahub.core.platform.connectivity.NetworkMonitor
import com.msa.msahub.core.platform.device.BatteryStatusProvider
import com.msa.msahub.core.platform.device.BatteryStatusProviderImpl
import com.msa.msahub.core.platform.device.DeviceInfoProvider
import com.msa.msahub.core.platform.device.DeviceInfoProviderImpl
import com.msa.msahub.core.platform.notification.AppNotificationCenter
import com.msa.msahub.core.platform.notification.MsaAppNotificationCenter
import com.msa.msahub.core.platform.notification.MsaNotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object PlatformModule {
    val module = module {
        single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }
        single<DeviceInfoProvider> { DeviceInfoProviderImpl() }
        single<BatteryStatusProvider> { BatteryStatusProviderImpl(androidContext()) }
        single { MsaNotificationManager(androidContext()) }
        single<AppNotificationCenter> { MsaAppNotificationCenter(notificationManager = get()) }
    }
}
