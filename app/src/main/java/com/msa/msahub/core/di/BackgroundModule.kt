package com.msa.msahub.core.di

import com.msa.msahub.background.scheduler.WorkScheduler
import com.msa.msahub.background.scheduler.WorkSchedulerImpl
import com.msa.msahub.features.devices.domain.usecase.FlushOfflineCommandsUseCase
import org.koin.dsl.module

val backgroundModule = module {
    single<WorkScheduler> { WorkSchedulerImpl(get()) }

    // ✅ Required by OfflineOutboxWorker (GlobalContext.get().get())
    factory { FlushOfflineCommandsUseCase(get()) }
}
