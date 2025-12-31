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

        // اصلاح نحوه فراخوانی Koin برای سازگاری با نسخه جدید
        val scheduler = GlobalContext.get().get<WorkScheduler>()
        scheduler.scheduleDataCleanup()
        scheduler.scheduleAnalyticsUpload()
    }
}
