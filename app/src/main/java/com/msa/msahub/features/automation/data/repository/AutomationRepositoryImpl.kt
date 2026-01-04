package com.msa.msahub.features.automation.data.repository

import com.msa.msahub.features.automation.data.local.dao.AutomationDao
import com.msa.msahub.features.automation.data.local.entity.AutomationEntity
import com.msa.msahub.features.automation.domain.model.Automation
import com.msa.msahub.features.automation.domain.repository.AutomationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AutomationRepositoryImpl(
    private val dao: AutomationDao,
    private val json: Json
) : AutomationRepository {

    override fun getAutomations(): Flow<List<Automation>> {
        return dao.getAllAutomations().map { entities ->
            entities.map { it.toDomain(json) }
        }
    }

    override suspend fun saveAutomation(automation: Automation) {
        dao.insert(automation.toEntity(json))
    }

    override suspend fun toggleAutomation(id: String, isEnabled: Boolean) {
        dao.setEnabled(id, isEnabled)
    }

    override suspend fun deleteAutomation(automation: Automation) {
        dao.deleteById(automation.id)
    }
}

// Mapper extensions
private fun AutomationEntity.toDomain(json: Json): Automation {
    return Automation(
        id = this.id,
        name = this.name,
        isEnabled = this.isEnabled,
        triggers = try { json.decodeFromString(this.triggerJson) } catch (e: Exception) { emptyList() },
        condition = try { this.conditionJson?.let { json.decodeFromString(it) } } catch (e: Exception) { null },
        actions = try { json.decodeFromString(this.actionsJson) } catch (e: Exception) { emptyList() }
    )
}

private fun Automation.toEntity(json: Json): AutomationEntity {
    return AutomationEntity(
        id = this.id,
        name = this.name,
        isEnabled = this.isEnabled,
        triggerJson = json.encodeToString(this.triggers),
        conditionJson = this.condition?.let { json.encodeToString(it) },
        actionsJson = json.encodeToString(this.actions),
        createdAt = System.currentTimeMillis()
    )
}
