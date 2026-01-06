package com.msa.msahub.features.automation.di

import com.msa.msahub.core.di.AppScopeModule
import com.msa.msahub.core.observability.NotificationHelper
import com.msa.msahub.features.automation.data.repository.AutomationRepositoryImpl
import com.msa.msahub.features.automation.domain.AutomationEngine
import com.msa.msahub.features.automation.domain.repository.AutomationRepository
import com.msa.msahub.features.automation.presentation.AddAutomationViewModel
import com.msa.msahub.features.automation.presentation.AutomationListViewModel
import com.msa.msahub.features.automation.presentation.log.AutomationLogViewModel
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AutomationModule {
    val module = module {
        // Repository
        single<AutomationRepository> { AutomationRepositoryImpl(get(), get()) }

        // Helper
        single { NotificationHelper(get()) }

        // Engine
        single {
            AutomationEngine(
                mqttClient = get(),
                automationDao = get(),
                logDao = get(),
                deviceRepository = get<DeviceRepository>(),
                notificationHelper = get(),
                ids = get(),
                logger = get(),
                scope = get(named("app_scope"))
            )
        }

        // ViewModels
        viewModel { AutomationListViewModel(get()) }
        viewModel { AddAutomationViewModel(get(), get(), get()) }
        viewModel { AutomationLogViewModel(get()) }
    }
}
