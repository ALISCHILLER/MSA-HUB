package com.msa.msahub.core.platform.notification

interface AppNotificationCenter {
    fun showSimpleNotification(title: String, body: String)
    fun automationTriggered(automationName: String)
}

class MsaAppNotificationCenter(
    private val notificationManager: MsaNotificationManager
) : AppNotificationCenter {

    override fun showSimpleNotification(title: String, body: String) {
        notificationManager.showNotification(title, body)
    }

    override fun automationTriggered(automationName: String) {
        notificationManager.showNotification("Automation Triggered", "Automation '$automationName' was executed successfully.")
    }
}
