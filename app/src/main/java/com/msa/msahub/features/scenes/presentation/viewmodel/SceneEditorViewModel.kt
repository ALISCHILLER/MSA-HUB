package com.msa.msahub.features.scenes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.common.IdGenerator
import com.msa.msahub.core.common.Result
import com.msa.msahub.features.scenes.domain.model.Scene
import com.msa.msahub.features.scenes.domain.model.SceneAction
import com.msa.msahub.features.scenes.domain.repository.SceneRepository
import com.msa.msahub.features.scenes.domain.usecase.CreateSceneUseCase
import com.msa.msahub.features.scenes.domain.usecase.UpdateSceneUseCase
import com.msa.msahub.features.scenes.domain.usecase.ValidateSceneUseCase
import com.msa.msahub.features.scenes.presentation.state.SceneEditorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SceneEditorViewModel(
    private val repo: SceneRepository,
    private val create: CreateSceneUseCase,
    private val update: UpdateSceneUseCase,
    private val validate: ValidateSceneUseCase,
    private val ids: IdGenerator
) : ViewModel() {

    private val _state = MutableStateFlow(SceneEditorUiState())
    val state: StateFlow<SceneEditorUiState> = _state

    fun load(sceneId: String?) {
        if (sceneId.isNullOrBlank() || sceneId == "new") {
            _state.value = SceneEditorUiState(isLoading = false, sceneId = null)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, saved = false)
            when (val r = repo.getScene(sceneId)) {
                is Result.Failure -> _state.value = _state.value.copy(isLoading = false, error = r.error.message)
                is Result.Success -> {
                    val s = r.data
                    _state.value = _state.value.copy(
                        isLoading = false,
                        sceneId = s.id,
                        name = s.name,
                        enabled = s.enabled,
                        actions = s.actions
                    )
                }
            }
        }
    }

    fun setName(value: String) {
        _state.value = _state.value.copy(name = value, saved = false)
    }

    fun setEnabled(value: Boolean) {
        _state.value = _state.value.copy(enabled = value, saved = false)
    }

    fun addAction(deviceId: String, command: String, payload: String?) {
        val list = _state.value.actions.toMutableList()
        list.add(SceneAction(deviceId = deviceId, command = command, payload = payload))
        _state.value = _state.value.copy(actions = list, saved = false)
    }

    fun removeAction(index: Int) {
        val list = _state.value.actions.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _state.value = _state.value.copy(actions = list, saved = false)
        }
    }

    fun save() {
        viewModelScope.launch {
            val current = _state.value
            val scene = Scene(
                id = current.sceneId ?: ids.uuid(),
                name = current.name.trim(),
                enabled = current.enabled,
                actions = current.actions
            )

            when (val v = validate(scene)) {
                is Result.Failure -> {
                    _state.value = _state.value.copy(error = v.error.message)
                    return@launch
                }
                is Result.Success -> { /* continue */ }
            }

            _state.value = _state.value.copy(isLoading = true, error = null, saved = false)
            val result = if (current.sceneId == null) create(scene) else update(scene)

            when (result) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, saved = true, sceneId = scene.id)
                is Result.Failure -> _state.value = _state.value.copy(isLoading = false, error = result.error.message)
            }
        }
    }
}
