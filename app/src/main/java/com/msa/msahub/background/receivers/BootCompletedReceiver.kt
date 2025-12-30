package com.msa.msahub.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.msa.msahub.background.scheduler.WorkScheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class BootCompletedReceiver : BroadcastReceiver(), KoinComponent {

    private val scheduler: WorkScheduler by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.d("BootCompletedReceiver: System rebooted, scheduling background works.")
            scheduler.scheduleAll()
        }
    }
}
