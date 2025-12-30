package com.msa.msahub.background.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber

class ActiveControlService : Service() {

    override fun onCreate() {
        super.onCreate()
        Timber.d("ActiveControlService: onCreate")
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
}
