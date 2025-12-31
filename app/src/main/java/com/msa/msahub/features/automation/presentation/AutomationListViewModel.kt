package com.msa.msahub.features.automation.presentation

import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.ui.mvi.BaseViewModel
import com.msa.msahub.features.automation.domain.model.Automation
import com.msa.msahub.features.automation.domain.repository.AutomationRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class AutomationListState(
    val automations: List<Automation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface AutomationListEvent {
    data class ToggleAutomation(val id: String, val isEnabled: Boolean) : AutomationListEvent
    data class DeleteAutomation(val automation: Automation) : AutomationListEvent
}

sealed interface AutomationListEffect {
    data class ShowError(val message: String) : AutomationListEffect
}

class AutomationListViewModel(
    private val repository: AutomationRepository
) : BaseViewModel<AutomationListState, AutomationListEvent, AutomationListEffect>(AutomationListState()) {

    init {
        loadAutomations()
    }

    private fun loadAutomations() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            repository.getAutomations().collectLatest { list ->
                updateState { copy(automations = list, isLoading = false) }
            }
        }
    }

    override fun onEvent(event: AutomationListEvent) {
        when (event) {
            is AutomationListEvent.ToggleAutomation -> {
                viewModelScope.launch {
                    repository.toggleAutomation(event.id, event.isEnabled)
                }
            }
            is AutomationListEvent.DeleteAutomation -> {
                viewModelScope.launch {
                    repository.deleteAutomation(event.automation)
                }
            }
        }
    }
}
