package com.msa.msahub.core.di

import com.msa.msahub.core.common.Clock
import com.msa.msahub.core.common.SystemClock
import org.koin.dsl.module

object CoreModule {
    val module = module {
        single<Clock> { SystemClock() }
    }
}
