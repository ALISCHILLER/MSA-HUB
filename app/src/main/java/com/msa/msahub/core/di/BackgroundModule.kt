package com.msa.msahub.core.di

import androidx.work.WorkManager
import com.msa.msahub.background.scheduler.WorkScheduler
import com.msa.msahub.background.scheduler.WorkSchedulerImpl
import com.msa.msahub.background.services.ForegroundServiceController
import com.msa.msahub.background.services.ServiceNotifications
import org.koin.dsl.module

val backgroundModule = module {
    // WorkManager and worker factories
    single { WorkManager.getInstance(get()) }
    single<WorkScheduler> { WorkSchedulerImpl(get()) }
    factory { ServiceNotifications(get()) }
    factory { ForegroundServiceController(get()) }
}
