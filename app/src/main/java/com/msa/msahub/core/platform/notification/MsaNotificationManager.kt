package com.msa.msahub.core.platform.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MsaNotificationManager(private val context: Context) {

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // کانال هشدارهای عمومی
            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "MSA Hub Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for automation and device alerts"
            }
            notificationManager.createNotificationChannel(alertsChannel)

            // کانال سرویس‌های پیش‌زمینه
            val serviceChannel = NotificationChannel(
                CHANNEL_SERVICE,
                "Active Services",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Status of active background tasks"
            }
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    fun showNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    fun createForegroundNotification(title: String, message: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_SERVICE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val CHANNEL_ALERTS = "msa_hub_alerts"
        const val CHANNEL_SERVICE = "msa_hub_service"
    }
}
