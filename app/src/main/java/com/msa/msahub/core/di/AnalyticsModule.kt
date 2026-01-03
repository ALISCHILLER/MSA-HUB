package com.msa.msahub.core.di

import com.msa.msahub.core.observability.EventLogger
import com.msa.msahub.core.observability.TimberEventLogger
import org.koin.dsl.module

object AnalyticsModule {
    val module = module {
        single<EventLogger> { TimberEventLogger() }
    }
}
