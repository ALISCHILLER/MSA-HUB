package com.msa.msahub.features.automation.presentation

import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.common.IdGenerator
import com.msa.msahub.core.ui.mvi.BaseViewModel
import com.msa.msahub.features.automation.domain.model.*
import com.msa.msahub.features.automation.domain.repository.AutomationRepository
import com.msa.msahub.features.devices.domain.model.Device
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AddAutomationState(
    val currentStep: Int = 1,
    val name: String = "",
    val triggers: List<AutomationTrigger> = emptyList(),
    val condition: AutomationCondition? = null,
    val actions: List<AutomationAction> = emptyList(),
    val isSaving: Boolean = false,
    val availableDevices: List<Device> = emptyList()
)

sealed interface AddAutomationEvent {
    data class UpdateName(val name: String) : AddAutomationEvent
    data class AddTrigger(val trigger: AutomationTrigger) : AddAutomationEvent
    data class RemoveTrigger(val index: Int) : AddAutomationEvent
    data class SetCondition(val condition: AutomationCondition?) : AddAutomationEvent
    data class AddAction(val action: AutomationAction) : AddAutomationEvent
    data object NextStep : AddAutomationEvent
    data object PreviousStep : AddAutomationEvent
    data object Save : AddAutomationEvent
    data object LoadDevices : AddAutomationEvent
}

sealed interface AddAutomationEffect {
    data object NavigateBack : AddAutomationEffect
    data class ShowError(val message: String) : AddAutomationEffect
}

class AddAutomationViewModel(
    private val repository: AutomationRepository,
    private val deviceRepository: DeviceRepository,
    private val ids: IdGenerator
) : BaseViewModel<AddAutomationState, AddAutomationEvent, AddAutomationEffect>(AddAutomationState()) {

    init {
        onEvent(AddAutomationEvent.LoadDevices)
    }

    override fun onEvent(event: AddAutomationEvent) {
        when (event) {
            is AddAutomationEvent.UpdateName -> updateState { copy(name = event.name) }
            is AddAutomationEvent.AddTrigger -> updateState { copy(triggers = triggers + event.trigger) }
            is AddAutomationEvent.RemoveTrigger -> updateState { 
                val newList = triggers.toMutableList().apply { removeAt(event.index) }
                copy(triggers = newList)
            }
            is AddAutomationEvent.SetCondition -> updateState { copy(condition = event.condition) }
            is AddAutomationEvent.AddAction -> updateState { copy(actions = actions + event.action) }
            AddAutomationEvent.NextStep -> updateState { copy(currentStep = (currentStep + 1).coerceAtMost(3)) }
            AddAutomationEvent.PreviousStep -> updateState { copy(currentStep = (currentStep - 1).coerceAtLeast(1)) }
            AddAutomationEvent.Save -> saveAutomation()
            AddAutomationEvent.LoadDevices -> loadDevices()
        }
    }

    private fun loadDevices() {
        viewModelScope.launch {
            val devices = deviceRepository.observeDevices().first()
            updateState { copy(availableDevices = devices) }
        }
    }

    private fun saveAutomation() {
        val state = uiState.value
        if (state.name.isBlank() || state.triggers.isEmpty() || state.actions.isEmpty()) {
            viewModelScope.launch { emitEffect(AddAutomationEffect.ShowError("لطفاً تمام موارد ضروری را پر کنید")) }
            return
        }

        updateState { copy(isSaving = true) }
        viewModelScope.launch {
            val newAutomation = Automation(
                id = ids.uuid(),
                name = state.name,
                isEnabled = true,
                triggers = state.triggers,
                condition = state.condition,
                actions = state.actions
            )
            repository.saveAutomation(newAutomation)
            emitEffect(AddAutomationEffect.NavigateBack)
        }
    }
}
