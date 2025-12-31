package com.msa.msahub.core.di

import com.msa.msahub.core.common.*
import org.koin.dsl.module

object CoreModule {
    val module = module {
        single<Logger> { TimberLogger() }
        single<DispatcherProvider> { DefaultDispatcherProvider() }
        single<IdGenerator> { UUIDGenerator() }
        single<Clock> { com.msa.msahub.core.common.SystemClock() }
    }
}
