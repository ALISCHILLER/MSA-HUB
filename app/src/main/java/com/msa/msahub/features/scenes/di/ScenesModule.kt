package com.msa.msahub.features.scenes.di

import com.msa.msahub.features.scenes.data.repository.SceneRepositoryImpl
import com.msa.msahub.features.scenes.domain.repository.SceneRepository
import com.msa.msahub.features.scenes.domain.usecase.*
import com.msa.msahub.features.scenes.presentation.viewmodel.SceneListViewModel
import com.msa.msahub.features.scenes.presentation.viewmodel.SceneEditorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ScenesModule {
    val module = module {
        single<SceneRepository> { SceneRepositoryImpl() }
        factory { GetScenesUseCase(get()) }
        factory { CreateSceneUseCase(get()) }
        factory { UpdateSceneUseCase(get()) }
        factory { ExecuteSceneUseCase(get()) }
        factory { ValidateSceneUseCase(get()) }
        viewModel { SceneListViewModel(get(), get()) }
        viewModel { SceneEditorViewModel(get(), get(), get()) }
    }
}
