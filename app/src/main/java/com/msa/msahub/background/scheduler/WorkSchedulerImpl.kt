package com.msa.msahub.background.scheduler

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.msa.msahub.background.workers.OfflineOutboxWorker
import java.util.concurrent.TimeUnit

class WorkSchedulerImpl(
    private val context: Context
) : WorkScheduler {

    override fun scheduleAll() {
        scheduleOutboxPeriodic()
    }

    override fun scheduleOutboxNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val req = OneTimeWorkRequestBuilder<OfflineOutboxWorker>()
            .setConstraints(constraints)
            .addTag(WorkTags.OFFLINE_OUTBOX)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WorkNames.OFFLINE_OUTBOX_NOW,
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

    override fun scheduleOutboxPeriodic() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val req = PeriodicWorkRequestBuilder<OfflineOutboxWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(WorkTags.OFFLINE_OUTBOX)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WorkNames.OFFLINE_OUTBOX_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            req
        )
    }
}
