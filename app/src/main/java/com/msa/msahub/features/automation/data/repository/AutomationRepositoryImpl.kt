package com.msa.msahub.features.automation.data.repository

import com.msa.msahub.features.automation.data.local.dao.AutomationDao
import com.msa.msahub.features.automation.data.local.entity.AutomationEntity
import com.msa.msahub.features.automation.domain.model.Automation
import com.msa.msahub.features.automation.domain.model.AutomationTrigger
import com.msa.msahub.features.automation.domain.model.Operator
import com.msa.msahub.features.automation.domain.repository.AutomationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class AutomationRepositoryImpl(
    private val automationDao: AutomationDao
) : AutomationRepository {

    override fun observeAutomations(): Flow<List<Automation>> {
        return automationDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveAutomation(automation: Automation) {
        automationDao.upsert(automation.toEntity())
    }

    override suspend fun deleteAutomation(id: String) {
        automationDao.deleteById(id)
    }

    // --- Mappers ---
    
    private fun AutomationEntity.toDomain(): Automation {
        val sceneIds = JSONArray(sceneIdsJson)
        val list = mutableListOf<String>()
        for (i in 0 until sceneIds.length()) list.add(sceneIds.getString(i))

        val triggerJson = JSONObject(triggerConfigJson)
        val trigger = if (triggerType == "DEVICE") {
            AutomationTrigger.DeviceState(
                deviceId = triggerJson.getString("deviceId"),
                key = triggerJson.getString("key"),
                operator = Operator.valueOf(triggerJson.getString("operator")),
                value = triggerJson.getString("value")
            )
        } else {
            AutomationTrigger.Time(
                hour = triggerJson.getInt("hour"),
                minute = triggerJson.getInt("minute"),
                daysOfWeek = listOf() // Simplified
            )
        }

        return Automation(id, name, enabled, trigger, list, createdAt)
    }

    private fun Automation.toEntity(): AutomationEntity {
        val triggerJson = JSONObject()
        val type = when (val t = trigger) {
            is AutomationTrigger.DeviceState -> {
                triggerJson.put("deviceId", t.deviceId)
                triggerJson.put("key", t.key)
                triggerJson.put("operator", t.operator.name)
                triggerJson.put("value", t.value)
                "DEVICE"
            }
            is AutomationTrigger.Time -> {
                triggerJson.put("hour", t.hour)
                triggerJson.put("minute", t.minute)
                "TIME"
            }
        }

        return AutomationEntity(
            id = id,
            name = name,
            enabled = enabled,
            triggerType = type,
            triggerConfigJson = triggerJson.toString(),
            sceneIdsJson = JSONArray(actions).toString(),
            createdAt = createdAt
        )
    }
}
