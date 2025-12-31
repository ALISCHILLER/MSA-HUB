package com.msa.msahub.features.automation.domain.repository

import com.msa.msahub.features.automation.domain.model.Automation
import kotlinx.coroutines.flow.Flow

interface AutomationRepository {
    fun observeAutomations(): Flow<List<Automation>>
    suspend fun saveAutomation(automation: Automation)
    suspend fun deleteAutomation(id: String)
}
