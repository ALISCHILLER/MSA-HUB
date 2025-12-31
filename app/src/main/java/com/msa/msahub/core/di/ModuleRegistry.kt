package com.msa.msahub.core.di

import com.msa.msahub.features.devices.di.DevicesModule
import com.msa.msahub.features.home.di.HomeModule
import com.msa.msahub.features.scenes.di.ScenesModule
import com.msa.msahub.features.settings.di.SettingsModule
import org.koin.core.module.Module

object ModuleRegistry {
    val allModules: List<Module> = listOf(
        CoreModule.module,
        PlatformModule.module,
        NetworkModule.module,
        DatabaseModule.module,
        AppScopeModule.module,
        BackgroundModule.module,
        SecurityModule.module, // اضافه شد
        HomeModule.module,
        DevicesModule.module,
        ScenesModule.module,
        SettingsModule.module // اضافه شد
    )
}
