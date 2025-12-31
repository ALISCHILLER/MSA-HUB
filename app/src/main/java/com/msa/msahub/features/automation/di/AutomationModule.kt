package com.msa.msahub.features.automation.di

import com.msa.msahub.features.automation.data.local.dao.AutomationDao
import com.msa.msahub.features.automation.data.local.dao.AutomationLogDao
import com.msa.msahub.features.automation.domain.AutomationEngine
import com.msa.msahub.features.automation.domain.repository.AutomationRepository
import com.msa.msahub.features.automation.data.repository.AutomationRepositoryImpl
import com.msa.msahub.features.automation.presentation.AutomationListViewModel
import com.msa.msahub.features.automation.presentation.AddAutomationViewModel
import com.msa.msahub.features.automation.presentation.log.AutomationLogViewModel
import com.msa.msahub.core.di.AppScopeModule
import com.msa.msahub.core.observability.NotificationHelper
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import org.koin.core.qualifier.named
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AutomationModule {
    val module = module {
        // DAOs
        single<AutomationDao> { get<com.msa.msahub.core.platform.database.AppDatabase>().automationDao() }
        single<AutomationLogDao> { get<com.msa.msahub.core.platform.database.AppDatabase>().automationLogDao() }

        // Repository
        single<AutomationRepository> { AutomationRepositoryImpl(get()) }

        // Helper
        single { NotificationHelper(get()) }

        // Engine
        single {
            AutomationEngine(
                mqttClient = get(),
                automationDao = get(),
                logDao = get(),
                deviceRepository = get<DeviceRepository>(),
                outbox = get(),
                notificationHelper = get(),
                ids = get(),
                logger = get(),
                scope = get<kotlinx.coroutines.CoroutineScope>(named(AppScopeModule.APP_SCOPE))
            )
        }

        // ViewModels
        viewModel { AutomationListViewModel(get()) }
        viewModel { AddAutomationViewModel(get(), get()) }
        viewModel { AutomationLogViewModel(get()) }
    }
}
