package com.msa.msahub.features.automation.data.engine

import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.features.automation.data.local.dao.AutomationDao
import com.msa.msahub.features.automation.domain.model.*
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import com.msa.msahub.core.common.IdGenerator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.util.concurrent.ConcurrentHashMap

/**
 * Optimized Automation Engine
 * Uses in-memory caching and reactive database tracking to avoid redundant DB queries.
 */
class AutomationEngine(
    private val mqttClient: MqttClient,
    private val automationDao: AutomationDao,
    private val deviceRepository: DeviceRepository,
    private val logger: Logger,
    private val scope: CoroutineScope
) {
    private val json = Json { ignoreUnknownKeys = true }

    // کش کردن اتوماسیون‌ها برای دسترسی سریع (Key: DeviceId)
    // این باعث می‌شود به محض رسیدن پیام از یک دستگاه، فقط اتوماسیون‌های مربوط به آن را چک کنیم.
    private val automationCache = ConcurrentHashMap<String, MutableList<Pair<String, AutomationTrigger>>>()
    private val fullEntities = ConcurrentHashMap<String, com.msa.msahub.features.automation.data.local.entity.AutomationEntity>()

    fun start() {
        // ۱. مانیتور کردن تغییرات دیتابیس (Reactive Cache)
        scope.launch {
            automationDao.observeEnabledAutomations().collect { entities ->
                updateCache(entities)
            }
        }

        // ۲. پردازش پیام‌های MQTT از روی کش RAM
        scope.launch {
            mqttClient.incomingMessages.collect { message ->
                processIncomingMessage(message.topic, String(message.payload))
            }
        }
    }

    private fun updateCache(entities: List<com.msa.msahub.features.automation.data.local.entity.AutomationEntity>) {
        automationCache.clear()
        fullEntities.clear()

        for (entity in entities) {
            fullEntities[entity.id] = entity
            val trigger = runCatching { json.decodeFromString<AutomationTrigger>(entity.triggerJson) }.getOrNull()
            
            if (trigger is AutomationTrigger.DeviceStateChanged) {
                val list = automationCache.getOrPut(trigger.deviceId) { mutableListOf() }
                list.add(entity.id to trigger)
            }
        }
        logger.d("Automation cache updated: ${entities.size} active rules.")
    }

    private suspend fun processIncomingMessage(topic: String, payload: String) {
        // فرض می‌کنیم فرمت تاپیک: devices/{deviceId}/status است
        val deviceIdFromTopic = extractDeviceId(topic) ?: return
        
        // فقط اتوماسیون‌هایی را چک می‌کنیم که مربوط به این DeviceId هستند
        val relevantTriggers = automationCache[deviceIdFromTopic] ?: return

        for ((id, trigger) in relevantTriggers) {
            if (isTriggerMatched(trigger, payload)) {
                val entity = fullEntities[id] ?: continue
                logger.i("Trigger matched for automation: ${entity.name}")
                executeAutomation(entity)
            }
        }
    }

    private fun isTriggerMatched(trigger: AutomationTrigger, payload: String): Boolean {
        return when (trigger) {
            is AutomationTrigger.DeviceStateChanged -> {
                // اینجا می‌توان منطق پیچیده‌تری مثل چک کردن مقدار (value >= threshold) اضافه کرد
                payload.contains(trigger.attribute)
            }
            else -> false
        }
    }

    private suspend fun executeAutomation(entity: com.msa.msahub.features.automation.data.local.entity.AutomationEntity) {
        val actions = runCatching { json.decodeFromString<List<AutomationAction>>(entity.actionsJson) }.getOrDefault(emptyList())
        
        for (action in actions) {
            val cmd = com.msa.msahub.features.devices.domain.model.DeviceCommand(
                deviceId = action.deviceId,
                action = action.command,
                params = action.params,
                createdAtMillis = System.currentTimeMillis()
            )
            // ارسال فرمان (که خودش Outbox دارد و امن است)
            deviceRepository.sendCommand(cmd)
        }
    }

    private fun extractDeviceId(topic: String): String? {
        // منطق استخراج ID از تاپیک (مثال: devices/123/status -> 123)
        val segments = topic.split("/")
        return if (segments.size >= 2) segments[1] else null
    }
}
