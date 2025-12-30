package com.msa.msahub.features.scenes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.common.Result
import com.msa.msahub.features.scenes.domain.usecase.ExecuteSceneUseCase
import com.msa.msahub.features.scenes.domain.usecase.GetScenesUseCase
import com.msa.msahub.features.scenes.presentation.state.SceneListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SceneListViewModel(
    private val getScenes: GetScenesUseCase,
    private val executeScene: ExecuteSceneUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SceneListUiState())
    val state: StateFlow<SceneListUiState> = _state

    init {
        observe()
    }

    private fun observe() {
        viewModelScope.launch {
            getScenes()
                .onEach { scenes ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        scenes = scenes,
                        error = null
                    )
                }
                .catch { t ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = t.message ?: "Unknown error"
                    )
                }
                .collect { }
        }
    }

    fun run(sceneId: String) {
        viewModelScope.launch {
            when (val r = executeScene(sceneId)) {
                is Result.Success -> { /* no-op */ }
                is Result.Failure -> _state.value = _state.value.copy(error = r.error.message)
            }
        }
    }
}
