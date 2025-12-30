package com.msa.msahub.core.di

import com.msa.msahub.background.scheduler.WorkScheduler
import com.msa.msahub.background.scheduler.WorkSchedulerImpl
import org.koin.dsl.module

object BackgroundModule {
    val module = module {
        single<WorkScheduler> { WorkSchedulerImpl(get(), get()) }
    }
}
