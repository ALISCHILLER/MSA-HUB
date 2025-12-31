package com.msa.msahub.features.automation.data.repository

import com.msa.msahub.features.automation.data.local.dao.AutomationDao
import com.msa.msahub.features.automation.data.local.entity.AutomationEntity
import com.msa.msahub.features.automation.domain.model.Automation
import com.msa.msahub.features.automation.domain.repository.AutomationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class AutomationRepositoryImpl(
    private val dao: AutomationDao
) : AutomationRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getAutomations(): Flow<List<Automation>> {
        return dao.getAllAutomations().map { entities ->
            entities.map { entity ->
                Automation(
                    id = entity.id,
                    name = entity.name,
                    isEnabled = entity.isEnabled,
                    trigger = json.decodeFromString(entity.triggerJson),
                    condition = entity.conditionJson?.let { json.decodeFromString(it) },
                    actions = json.decodeFromString(entity.actionsJson)
                )
            }
        }
    }

    override suspend fun saveAutomation(automation: Automation) {
        val entity = AutomationEntity(
            id = automation.id,
            name = automation.name,
            isEnabled = automation.isEnabled,
            triggerJson = json.encodeToString(automation.trigger),
            conditionJson = automation.condition?.let { json.encodeToString(it) },
            actionsJson = json.encodeToString(automation.actions)
        )
        dao.insert(entity)
    }

    override suspend fun deleteAutomation(automation: Automation) {
        // حذف از دیتابیس
    }

    override suspend fun toggleAutomation(id: String, isEnabled: Boolean) {
        dao.setEnabled(id, isEnabled)
    }
}
