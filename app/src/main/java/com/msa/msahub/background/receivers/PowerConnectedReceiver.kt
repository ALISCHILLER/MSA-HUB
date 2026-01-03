package com.msa.msahub.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.msa.msahub.background.scheduler.WorkScheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PowerConnectedReceiver : BroadcastReceiver(), KoinComponent {

    private val scheduler: WorkScheduler by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_POWER_CONNECTED) {
            return
        }

        scheduler.scheduleDataCleanup()
        scheduler.scheduleAnalyticsUpload()
    }
}
