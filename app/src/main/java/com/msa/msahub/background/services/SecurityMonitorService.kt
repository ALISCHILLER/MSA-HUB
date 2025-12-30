package com.msa.msahub.background.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber

class SecurityMonitorService : Service() {
    override fun onCreate() {
        super.onCreate()
        Timber.d("SecurityMonitorService: System integrity check active")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
