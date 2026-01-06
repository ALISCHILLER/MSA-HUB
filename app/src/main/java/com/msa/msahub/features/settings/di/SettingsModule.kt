package com.msa.msahub.features.settings.di

import com.msa.msahub.features.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object SettingsModule {
    val module = module {
        viewModel { SettingsViewModel(get(), get(), get()) }
    }
}
