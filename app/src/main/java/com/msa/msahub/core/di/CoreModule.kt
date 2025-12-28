package com.msa.msahub.core.di

import com.msa.msahub.core.common.*
import org.koin.dsl.module

val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<Logger> { AppLogger() }
    single<Clock> { SystemClock() }
    single<IdGenerator> { UUIDGenerator() }
}
