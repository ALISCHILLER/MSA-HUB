package com.msa.msahub.core.di

import com.msa.msahub.core.observability.EventLogger
import com.msa.msahub.core.observability.TimberEventLogger
import com.msa.msahub.features.analytics.data.engine.AnalyticsEngine
import org.koin.dsl.module

object AnalyticsModule {
    val module = module {
        single<EventLogger> { TimberEventLogger() }
        single { AnalyticsEngine(deviceDao = get(), stateDao = get(), analyticsDao = get(), logger = get()) }
    }
}
