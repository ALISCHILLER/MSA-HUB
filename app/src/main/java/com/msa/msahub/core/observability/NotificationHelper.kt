package com.msa.msahub.core.observability

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.msa.msahub.R

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val automationChannel = NotificationChannel(
                CHANNEL_AUTOMATION,
                "اجرای اتوماسیون",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "گزارش اجرای سناریوهای هوشمند"
            }
            notificationManager.createNotificationChannel(automationChannel)
        }
    }

    fun showAutomationNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_AUTOMATION)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // از آیکون مناسب پروژه استفاده شود
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val CHANNEL_AUTOMATION = "automation_channel"
    }
}
