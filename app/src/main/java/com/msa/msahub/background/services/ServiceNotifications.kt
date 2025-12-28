package com.msa.msahub.background.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.msa.msahub.R

class ServiceNotifications(private val context: Context) {

    fun activeControlNotification(): Notification {
        ensureChannels()
        return NotificationCompat.Builder(context, CHANNEL_ACTIVE_CONTROL)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Active control session is running.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    fun securityMonitorNotification(): Notification {
        ensureChannels()
        return NotificationCompat.Builder(context, CHANNEL_SECURITY)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Security monitoring is active.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    private fun ensureChannels() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ACTIVE_CONTROL) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ACTIVE_CONTROL,
                    "Active Control",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
        if (manager.getNotificationChannel(CHANNEL_SECURITY) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_SECURITY,
                    "Security Monitor",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    companion object {
        const val ACTIVE_CONTROL_NOTIFICATION_ID = 1001
        const val SECURITY_NOTIFICATION_ID = 1002

        const val CHANNEL_ACTIVE_CONTROL = "channel_active_control"
        const val CHANNEL_SECURITY = "channel_security"
    }
}