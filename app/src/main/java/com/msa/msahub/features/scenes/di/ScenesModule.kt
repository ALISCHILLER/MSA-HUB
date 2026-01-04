package com.msa.msahub.features.scenes.di

import com.msa.msahub.features.scenes.data.mapper.SceneMapper
import com.msa.msahub.features.scenes.data.repository.SceneRepositoryImpl
import com.msa.msahub.features.scenes.domain.repository.SceneRepository
import com.msa.msahub.features.scenes.domain.usecase.*
import com.msa.msahub.features.scenes.presentation.viewmodel.SceneEditorViewModel
import com.msa.msahub.features.scenes.presentation.viewmodel.SceneListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ScenesModule {
    val module = module {
        single { SceneMapper(get()) }

        single<SceneRepository> {
            SceneRepositoryImpl(
                dao = get(),
                mapper = get(),
                mqtt = get(),
                offlineDao = get(),
                clock = get(),
                ids = get()
            )
        }

        factory { ValidateSceneUseCase() }
        factory { GetScenesUseCase(get()) }
        factory { ExecuteSceneUseCase(get()) }
        factory { CreateSceneUseCase(get(), get()) }
        factory { UpdateSceneUseCase(get(), get()) }

        viewModel { SceneListViewModel(get(), get()) }
        viewModel { SceneEditorViewModel(get(), get(), get(), get(), get()) }
    }
}
