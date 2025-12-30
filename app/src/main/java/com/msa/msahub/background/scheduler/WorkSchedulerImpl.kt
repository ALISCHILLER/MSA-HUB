package com.msa.msahub.background.scheduler

import android.content.Context
import androidx.work.*
import com.msa.msahub.background.workers.*
import java.util.concurrent.TimeUnit

class WorkSchedulerImpl(
    private val context: Context,
    private val workManager: WorkManager
) : WorkScheduler {

    override fun scheduleAll() {
        schedulePeriodicSync()
        scheduleDataCleanup()
        scheduleOfflineOutbox()
        scheduleConnectionHealthCheck()
    }

    override fun schedulePeriodicSync() {
        val req = PeriodicWorkRequestBuilder<PeriodicSyncWorker>(15, TimeUnit.MINUTES)
            .addTag(WorkTags.SYNC)
            .build()
        workManager.enqueueUniquePeriodicWork(WorkNames.PERIODIC_SYNC, ExistingPeriodicWorkPolicy.KEEP, req)
    }

    override fun scheduleOneTimeSync() {
        val req = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueueUniqueWork("ONE_TIME_SYNC", ExistingWorkPolicy.REPLACE, req)
    }

    override fun scheduleOfflineOutbox() {
        scheduleOutboxPeriodic()
    }

    override fun scheduleOutboxNow() {
        val req = OneTimeWorkRequestBuilder<OfflineOutboxWorker>().build()
        workManager.enqueueUniqueWork("OUTBOX_NOW", ExistingWorkPolicy.REPLACE, req)
    }

    override fun scheduleOutboxPeriodic() {
        val req = PeriodicWorkRequestBuilder<OfflineOutboxWorker>(15, TimeUnit.MINUTES)
            .addTag(WorkTags.OFFLINE_OUTBOX)
            .build()
        workManager.enqueueUniquePeriodicWork(WorkNames.OFFLINE_OUTBOX_NAME, ExistingPeriodicWorkPolicy.KEEP, req)
    }

    override fun scheduleDataCleanup() {
        val req = PeriodicWorkRequestBuilder<DataCleanupWorker>(24, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(WorkNames.DATA_CLEANUP, ExistingPeriodicWorkPolicy.KEEP, req)
    }

    override fun scheduleConnectionHealthCheck() {
        val req = PeriodicWorkRequestBuilder<ConnectionHealthWorker>(30, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(WorkNames.CONNECTION_HEALTH, ExistingPeriodicWorkPolicy.KEEP, req)
    }

    override fun scheduleAnalyticsUpload() {
        // Placeholder
    }

    override fun cancelAllWork() {
        workManager.cancelAllWork()
    }
}
