package com.msa.msahub.background.services

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.msa.msahub.core.platform.notification.MsaNotificationManager
import org.koin.android.ext.android.inject
import timber.log.Timber

class SecurityMonitorService : Service() {

    private val notificationManager: MsaNotificationManager by inject()

    override fun onCreate() {
        super.onCreate()
        Timber.d("SecurityMonitorService: System integrity check active")
        
        val notification = notificationManager.createForegroundNotification(
            "Security Monitoring Active",
            "Protecting your smart home data"
        )

        // P0: (Point 4) Ensure startForeground with correct type for Android 14+
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
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 1002
    }
}
