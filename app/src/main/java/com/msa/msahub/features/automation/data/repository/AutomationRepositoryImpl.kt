package com.msa.msahub.features.automation.data.repository

import com.msa.msahub.features.automation.data.local.dao.AutomationDao
import com.msa.msahub.features.automation.domain.model.Automation
import com.msa.msahub.features.automation.domain.repository.AutomationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AutomationRepositoryImpl(
    private val dao: AutomationDao
) : AutomationRepository {

    override fun getAutomations(): Flow<List<Automation>> {
        return dao.getAllAutomations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addAutomation(automation: Automation) {
        dao.insert(automation.toEntity())
    }

    override suspend fun toggleAutomation(id: String, isEnabled: Boolean) {
        dao.setEnabled(id, isEnabled)
    }

    override suspend fun deleteAutomation(automation: Automation) {
        dao.deleteById(automation.id)
    }

    // Helper to convert Entity to Domain model (assuming it's defined in the entity class)
    private fun com.msa.msahub.features.automation.data.local.entity.AutomationEntity.toDomain(): Automation {
        return Automation(this.id, this.name, this.isEnabled, emptyList(), emptyList()) // Triggers/Actions need to be mapped
    }

    private fun Automation.toEntity(): com.msa.msahub.features.automation.data.local.entity.AutomationEntity {
        return com.msa.msahub.features.automation.data.local.entity.AutomationEntity(
            id = this.id,
            name = this.name,
            isEnabled = this.isEnabled,
            triggersJson = "[]", // Triggers/Actions need to be serialized
            actionsJson = "[]",
            createdAt = System.currentTimeMillis()
        )
    }
}
