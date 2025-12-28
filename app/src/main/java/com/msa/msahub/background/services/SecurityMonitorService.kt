package com.msa.msahub.background.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SecurityMonitorService : Service() {
    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notifications = ServiceNotifications(this)
        startForeground(
            ServiceNotifications.SECURITY_NOTIFICATION_ID,
            notifications.securityMonitorNotification()
        )
        return START_STICKY
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }
}