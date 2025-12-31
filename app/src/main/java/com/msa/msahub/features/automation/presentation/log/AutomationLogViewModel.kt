package com.msa.msahub.features.automation.presentation.log

import androidx.lifecycle.viewModelScope
import com.msa.msahub.core.ui.mvi.BaseViewModel
import com.msa.msahub.features.automation.data.local.dao.AutomationLogDao
import com.msa.msahub.features.automation.data.local.entity.AutomationLogEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class AutomationLogState(
    val logs: List<AutomationLogEntity> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface AutomationLogEvent {
    data object ClearLogs : AutomationLogEvent
}

class AutomationLogViewModel(
    private val logDao: AutomationLogDao
) : BaseViewModel<AutomationLogState, AutomationLogEvent, Unit>(AutomationLogState()) {

    init {
        loadLogs()
    }

    private fun loadLogs() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            logDao.getRecentLogs().collectLatest { list ->
                updateState { copy(logs = list, isLoading = false) }
            }
        }
    }

    override fun onEvent(event: AutomationLogEvent) {
        when (event) {
            AutomationLogEvent.ClearLogs -> {
                viewModelScope.launch {
                    logDao.clearOldLogs(System.currentTimeMillis() + 1000) // پاکسازی همه
                }
            }
        }
    }
}
