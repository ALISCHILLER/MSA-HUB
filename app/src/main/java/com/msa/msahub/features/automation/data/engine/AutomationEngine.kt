package com.msa.msahub.features.automation.data.engine

import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.features.automation.data.local.dao.AutomationDao
import com.msa.msahub.features.automation.domain.model.*
import com.msa.msahub.features.devices.data.sync.OfflineCommandOutbox
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import com.msa.msahub.core.common.IdGenerator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class AutomationEngine(
    private val mqttClient: MqttClient,
    private val automationDao: AutomationDao,
    private val deviceRepository: DeviceRepository,
    private val outbox: OfflineCommandOutbox,
    private val ids: IdGenerator,
    private val logger: Logger,
    private val scope: CoroutineScope
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun start() {
        scope.launch {
            mqttClient.incomingMessages.collectLatest { message ->
                processMessage(message.topic, String(message.payload))
            }
        }
    }

    private suspend fun processMessage(topic: String, payload: String) {
        val enabledAutomations = automationDao.getEnabledAutomations()
        
        for (entity in enabledAutomations) {
            val trigger = json.decodeFromString<AutomationTrigger>(entity.triggerJson)
            
            if (isTriggerMatched(trigger, topic, payload)) {
                logger.d("Automation triggered: ${entity.name}")
                executeAutomation(entity)
            }
        }
    }

    private fun isTriggerMatched(trigger: AutomationTrigger, topic: String, payload: String): Boolean {
        return when (trigger) {
            is AutomationTrigger.DeviceStateChanged -> {
                topic.contains(trigger.deviceId) && payload.contains(trigger.attribute)
            }
            else -> false
        }
    }

    private suspend fun executeAutomation(entity: com.msa.msahub.features.automation.data.local.entity.AutomationEntity) {
        val actions = json.decodeFromString<List<AutomationAction>>(entity.actionsJson)
        
        for (action in actions) {
            val cmd = com.msa.msahub.features.devices.domain.model.DeviceCommand(
                deviceId = action.deviceId,
                action = action.command,
                params = action.params,
                createdAtMillis = System.currentTimeMillis()
            )
            deviceRepository.sendCommand(cmd)
            logger.i("Automation executing action for device: ${action.deviceId}")
        }
    }
}
