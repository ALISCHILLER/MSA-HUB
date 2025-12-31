package com.msa.msahub.background.services

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.msa.msahub.core.platform.notification.MsaNotificationManager
import timber.log.Timber
import org.koin.android.ext.android.inject

class ActiveControlService : Service() {

    private val notificationManager: MsaNotificationManager by inject()

    override fun onCreate() {
        super.onCreate()
        Timber.d("ActiveControlService: onCreate")
        
        val notification = notificationManager.createForegroundNotification(
            "Active Control Enabled",
            "Monitoring and controlling devices in real-time"
        )

        // شروع سرویس به عنوان Foreground با رعایت اندروید ۱۴+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("ActiveControlService: onStartCommand")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Timber.d("ActiveControlService: onDestroy")
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
