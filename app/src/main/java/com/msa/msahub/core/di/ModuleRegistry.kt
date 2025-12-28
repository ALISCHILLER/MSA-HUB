package com.msa.msahub.core.di

import org.koin.core.module.Module

object ModuleRegistry {
    val allModules: List<Module> = listOf(
        CoreModule.module,
        PlatformModule.module,
        NetworkModule.module,
        DatabaseModule.module,
        SecurityModule.module,
        BackgroundModule.module,
        AnalyticsModule.module,
        NavigationModule.module
    )
}
