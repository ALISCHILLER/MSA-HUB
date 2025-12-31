package com.msa.msahub.features.home.di

import com.msa.msahub.features.home.presentation.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object HomeModule {
    val module = module {
        viewModel { HomeViewModel(get(), get()) }
    }
}
