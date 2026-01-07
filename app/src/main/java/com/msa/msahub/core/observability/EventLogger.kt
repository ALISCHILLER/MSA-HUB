package com.msa.msahub.core.observability

import com.msa.msahub.core.common.Logger
import timber.log.Timber

/**
 * سیستم لاگینگ پیشرفته برای ثبت رویدادهای سیستم.
 * این کلاس به شما کمک می‌کند تا جریان داده‌ها را در بخش‌های مختلف (MQTT, DB, UI) ردیابی کنید.
 */
class EventLogger(private val logger: Logger) {

    fun logMqtt(message: String, isError: Boolean = false) {
        val formatted = "[MQTT] $message"
        if (isError) logger.e(formatted) else logger.i(formatted)
    }

    fun logCommand(deviceId: String, action: String, status: String) {
        logger.i("[COMMAND] Device: $deviceId | Action: $action | Status: $status")
    }

    fun logAutomation(name: String, triggered: Boolean) {
        if (triggered) {
            logger.d("[AUTO] Automation Triggered: $name")
        }
    }

    fun logError(context: String, throwable: Throwable) {
        logger.e("[ERROR] @ $context: ${throwable.message}", throwable)
    }
}
