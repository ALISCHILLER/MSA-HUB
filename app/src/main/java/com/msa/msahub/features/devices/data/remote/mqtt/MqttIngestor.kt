package com.msa.msahub.features.devices.data.remote.mqtt

import com.msa.msahub.core.common.IdGenerator
import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionState
import com.msa.msahub.features.devices.data.local.dao.DeviceHistoryDao
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.entity.DeviceHistoryEntity
import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * مدیریت‌کننده دیتای ورودی از سنسورها و دستگاه‌ها.
 * وظیفه بروزرسانی دیتابیس محلی بر اساس پیام‌های MQTT را دارد.
 */
class MqttIngestor(
    private val mqttClient: MqttClient,
    private val deviceStateDao: DeviceStateDao,
    private val deviceHistoryDao: DeviceHistoryDao,
    private val ids: IdGenerator,
    private val scope: CoroutineScope,
    private val logger: Logger
) {

    fun start() {
        logger.i("[INGEST] Service starting...")

        // ۱. مدیریت اشتراک‌ها (Subscriptions) بر اساس وضعیت اتصال
        mqttClient.connectionState
            .onEach { state ->
                if (state is MqttConnectionState.Connected) {
                    logger.i("[INGEST] Connected to broker. Subscribing to: ${DeviceMqttTopics.ALL_DEVICES_STATUS}")
                    mqttClient.subscribe(DeviceMqttTopics.ALL_DEVICES_STATUS)
                }
            }
            .launchIn(scope)

        // ۲. پردازش پیام‌های ورودی با فیلتر دقیق
        mqttClient.incomingMessages
            .filter { DeviceMqttTopics.extractDeviceId(it.topic) != null }
            .onEach { msg ->
                val deviceId = DeviceMqttTopics.extractDeviceId(msg.topic)!!
                handleStatusMessage(deviceId, msg.payload)
            }
            .launchIn(scope)
    }

    private suspend fun handleStatusMessage(deviceId: String, payload: ByteArray) {
        try {
            val raw = payload.toString(Charsets.UTF_8)
            val now = System.currentTimeMillis()

            // استخراج وضعیت جدید (State)
            val newState = parseToEntity(deviceId, raw, now) ?: return

            // ۳. بهینه‌سازی (Deduplication):
            // فقط اگر وضعیت واقعاً تغییر کرده باشد، دیتابیس را آپدیت می‌کنیم.
            val lastState = deviceStateDao.getLatest(deviceId)
            
            if (isSameState(lastState, newState)) {
                // اگر دیتا تکراری است، فقط updatedAt را آپدیت کن (اختیاری) یا کلاً نادیده بگیر
                return 
            }

            // ۴. بروزرسانی دیتابیس
            deviceStateDao.upsert(newState)
            logger.d("[INGEST] State updated for $deviceId")

            // ۵. ثبت در تاریخچه (History) - فقط برای تغییرات واقعی
            deviceHistoryDao.insert(
                DeviceHistoryEntity(
                    id = ids.uuid(),
                    deviceId = deviceId,
                    stateId = newState.id,
                    recordedAtMillis = newState.updatedAtMillis
                )
            )
        } catch (e: Exception) {
            logger.e("[INGEST] Error processing message for $deviceId", e)
        }
    }

    private fun parseToEntity(deviceId: String, raw: String, now: Long): DeviceStateEntity? {
        return try {
            val json = JSONObject(raw)
            DeviceStateEntity(
                id = ids.uuid(),
                deviceId = deviceId,
                isOnline = json.optBoolean("online", true),
                isOn = if (json.has("on")) json.optBoolean("on") else null,
                brightness = if (json.has("brightness")) json.optInt("brightness") else null,
                temperatureC = if (json.has("temp")) json.optDouble("temp") else if (json.has("temperatureC")) json.optDouble("temperatureC") else null,
                humidityPercent = if (json.has("humidity")) json.optDouble("humidity") else null,
                batteryPercent = if (json.has("battery")) json.optInt("battery") else null,
                updatedAtMillis = json.optLong("timestamp", now)
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * مقایسه دو وضعیت برای تشخیص تغییرات واقعی.
     */
    private fun isSameState(old: DeviceStateEntity?, new: DeviceStateEntity): Boolean {
        if (old == null) return false
        return old.isOnline == new.isOnline &&
               old.isOn == new.isOn &&
               old.brightness == new.brightness &&
               old.temperatureC == new.temperatureC &&
               old.humidityPercent == new.humidityPercent &&
               old.batteryPercent == new.batteryPercent
    }
}
