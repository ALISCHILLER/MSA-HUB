package com.msa.msahub.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.msa.msahub.background.scheduler.WorkScheduler
import org.koin.core.context.GlobalContext

class PowerConnectedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_POWER_CONNECTED) {
            return
        }

        val scheduler = GlobalContext.get().koin.get<WorkScheduler>()
        scheduler.scheduleDataCleanup()
        scheduler.scheduleAnalyticsUpload()
    }
}
