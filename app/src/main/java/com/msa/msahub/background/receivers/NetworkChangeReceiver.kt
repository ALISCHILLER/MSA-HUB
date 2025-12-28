package com.msa.msahub.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.msa.msahub.background.scheduler.WorkScheduler
import org.koin.core.context.GlobalContext

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduler = GlobalContext.get().koin.get<WorkScheduler>()
        scheduler.scheduleOfflineOutbox()
        scheduler.scheduleOneTimeSync()
    }
}