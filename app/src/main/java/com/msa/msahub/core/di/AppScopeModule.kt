package com.msa.msahub.core.di

import com.msa.msahub.core.common.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AppScopeModule {
    val APP_SCOPE = named("app_scope")

    val module = module {
        single<DispatcherProvider> { DefaultDispatcherProvider() }
        single<IdGenerator> { UUIDGenerator() }
        single<Logger> { TimberLogger() }
        single<Clock> { SystemClock() }

        single(APP_SCOPE) {
            CoroutineScope(SupervisorJob() + get<DispatcherProvider>().io)
        }
    }
}
