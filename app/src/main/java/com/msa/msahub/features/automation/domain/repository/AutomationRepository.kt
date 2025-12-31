package com.msa.msahub.features.automation.domain.repository

import com.msa.msahub.features.automation.domain.model.Automation
import kotlinx.coroutines.flow.Flow

interface AutomationRepository {
    fun getAutomations(): Flow<List<Automation>>
    suspend fun saveAutomation(automation: Automation)
    suspend fun deleteAutomation(automation: Automation)
    suspend fun toggleAutomation(id: String, isEnabled: Boolean)
}
