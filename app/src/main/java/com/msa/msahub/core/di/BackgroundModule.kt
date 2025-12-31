package com.msa.msahub.core.di

import androidx.work.WorkManager
import com.msa.msahub.background.scheduler.WorkScheduler
import com.msa.msahub.background.scheduler.WorkSchedulerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object BackgroundModule {
    val module = module {
        single { WorkManager.getInstance(androidContext()) }
        single<WorkScheduler> { WorkSchedulerImpl(androidContext(), get(), get()) }
    }
}
