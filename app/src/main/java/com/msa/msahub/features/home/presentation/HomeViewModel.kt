package com.msa.msahub.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import com.msa.msahub.features.scenes.domain.repository.SceneRepository
import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.scenes.domain.model.Scene
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val favoriteDevices: List<Device> = emptyList(),
    val quickScenes: List<Scene> = emptyList(),
    val onlineCount: Int = 0,
    val totalCount: Int = 0
)

class HomeViewModel(
    private val deviceRepository: DeviceRepository,
    private val sceneRepository: SceneRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        _state.update { it.copy(isLoading = true) }
        
        combine(
            deviceRepository.observeDevices(),
            sceneRepository.observeScenes()
        ) { devices, scenes ->
            HomeUiState(
                isLoading = false,
                favoriteDevices = devices.filter { it.isFavorite },
                quickScenes = scenes.take(4), // فقط ۴ صحنه اول برای دسترسی سریع
                onlineCount = devices.count { it.lastSeenMillis > System.currentTimeMillis() - 300_000 },
                totalCount = devices.size
            )
        }.onEach { newState ->
            _state.value = newState
        }.launchIn(viewModelScope)
    }

    fun executeScene(sceneId: String) {
        viewModelScope.launch {
            sceneRepository.executeScene(sceneId)
        }
    }
}
