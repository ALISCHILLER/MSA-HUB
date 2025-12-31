package com.msa.msahub.features.automation.di

import com.msa.msahub.features.automation.data.local.dao.AutomationDao
import com.msa.msahub.features.automation.domain.AutomationEngine
import com.msa.msahub.core.di.AppScopeModule
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AutomationModule {
    val module = module {
        // فراهم کردن DAO از دیتابیس اصلی
        single { get<com.msa.msahub.core.platform.database.AppDatabase>().offlineCommandDao() } // برای استفاده در انجین
        
        // در صورتی که AutomationDao را به AppDatabase اضافه کردید:
        // single { get<com.msa.msahub.core.platform.database.AppDatabase>().automationDao() }

        single {
            AutomationEngine(
                mqttClient = get(),
                automationDao = get(), // نیازمند اضافه شدن به AppDatabase
                outbox = get(),
                ids = get(),
                logger = get(),
                scope = get(named(AppScopeModule.APP_SCOPE))
            )
        }
    }
}
