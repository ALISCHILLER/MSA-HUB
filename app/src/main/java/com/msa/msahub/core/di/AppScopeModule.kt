package com.msa.msahub.core.di

import com.msa.msahub.core.common.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AppScopeModule {
    val APP_SCOPE = named("app_scope")

    val module = module {
        // تعاریف Dispatcher, Logger و غیره به CoreModule منتقل شدند.
        single(APP_SCOPE) {
            CoroutineScope(SupervisorJob() + get<DispatcherProvider>().io)
        }
    }
}
