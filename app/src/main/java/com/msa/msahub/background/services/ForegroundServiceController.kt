package com.msa.msahub.background.services

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class ForegroundServiceController(private val context: Context) {

    fun startActiveControl() {
        val intent = Intent(context, ActiveControlService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopActiveControl() {
        val intent = Intent(context, ActiveControlService::class.java)
        context.stopService(intent)
    }

    fun startSecurityMonitor() {
        val intent = Intent(context, SecurityMonitorService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopSecurityMonitor() {
        val intent = Intent(context, SecurityMonitorService::class.java)
        context.stopService(intent)
    }
}