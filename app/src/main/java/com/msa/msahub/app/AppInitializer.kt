package com.msa.msahub.app

import android.app.Application
import com.msa.msahub.core.di.KoinInitializer
import timber.log.Timber

object AppInitializer {

    fun init(app: Application) {
        initLogging()
        KoinInitializer.init(app)
    }

    private fun initLogging() {
        if (Timber.forest().isEmpty()) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
