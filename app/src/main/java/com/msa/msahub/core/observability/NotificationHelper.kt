package com.msa.msahub.core.observability

import com.msa.msahub.core.platform.notification.AppNotificationCenter

@Deprecated("Use AppNotificationCenter (single notification source)")
class NotificationHelper(private val center: AppNotificationCenter) {

    fun showAutomationNotification(title: String, message: String) {
        center.automationTriggered(title, message)
    }
}
