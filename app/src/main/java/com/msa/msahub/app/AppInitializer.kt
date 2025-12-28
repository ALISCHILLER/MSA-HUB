package com.msa.msahub.app

import android.app.Application
import com.msa.msahub.background.scheduler.WorkScheduler
import com.msa.msahub.core.common.TimberLogger
import com.msa.msahub.core.di.KoinInitializer
import org.koin.core.context.GlobalContext
import timber.log.Timber

object AppInitializer {

    fun init(app: Application) {
        initLogging()
        KoinInitializer.init(app)
        GlobalContext.get().koin.get<WorkScheduler>().scheduleAll()
    }

    private fun initLogging() {
        if (Timber.treeCount() == 0) {
            Timber.plant(Timber.DebugTree())
        }
        TimberLogger
    }
}
