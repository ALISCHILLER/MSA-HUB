package com.msa.msahub.core.di

import com.msa.msahub.features.devices.di.DevicesModule
import com.msa.msahub.features.home.di.HomeModule
import org.koin.core.module.Module

object ModuleRegistry {
    val allModules: List<Module> = listOf(
        CoreModule.module,
        PlatformModule.module,
        NetworkModule.module,
        DatabaseModule.module,
        AppScopeModule.module,
        BackgroundModule.module,
        HomeModule.module,
        DevicesModule.module
    )
}
