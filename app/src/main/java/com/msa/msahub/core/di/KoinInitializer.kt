package com.msa.msahub.core.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

object KoinInitializer {
    fun init(context: Context) {
        startKoin {
            androidLogger()
            androidContext(context)
            modules(ModuleRegistry.allModules)
        }
    }
}
