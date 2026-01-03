package com.msa.msahub.background.scheduler

import android.content.Context
import androidx.work.*
import com.msa.msahub.background.workers.*
import com.msa.msahub.core.observability.EventLogger
import java.util.concurrent.TimeUnit

class WorkSchedulerImpl(
    private val context: Context,
    private val workManager: WorkManager,
    private val eventLogger: EventLogger
) : WorkScheduler {

    override fun scheduleAll() {
        schedulePeriodicSync()
        scheduleDataCleanup()
        scheduleOfflineOutbox()
        scheduleConnectionHealthCheck()
        scheduleAnalyticsUpload()
        eventLogger.logEvent("work_scheduler_all_scheduled")
    }

    override fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val req = PeriodicWorkRequestBuilder<PeriodicSyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkNames.PERIODIC_SYNC,
            ExistingPeriodicWorkPolicy.UPDATE,
            req
        )
    }

    override fun scheduleOfflineOutbox() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val req = PeriodicWorkRequestBuilder<OfflineOutboxWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkNames.OFFLINE_OUTBOX_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            req
        )
    }

    override fun scheduleDataCleanup() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresDeviceIdle(true)
            .build()

        val req = PeriodicWorkRequestBuilder<DataCleanupWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkNames.DATA_CLEANUP,
            ExistingPeriodicWorkPolicy.UPDATE,
            req
        )
    }

    override fun scheduleConnectionHealthCheck() {
        val req = PeriodicWorkRequestBuilder<ConnectionHealthWorker>(30, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkNames.CONNECTION_HEALTH,
            ExistingPeriodicWorkPolicy.UPDATE,
            req
        )
    }

    override fun scheduleAnalyticsUpload() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val req = PeriodicWorkRequestBuilder<AnalyticsUploadWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkNames.ANALYTICS_UPLOAD,
            ExistingPeriodicWorkPolicy.UPDATE,
            req
        )
    }

    override fun scheduleOneTimeSync() {
        val req = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueueUniqueWork("ONE_TIME_SYNC", ExistingWorkPolicy.REPLACE, req)
    }

    override fun scheduleOutboxNow() {
        val req = OneTimeWorkRequestBuilder<OfflineOutboxWorker>().build()
        workManager.enqueueUniqueWork("OUTBOX_FLUSH_NOW", ExistingWorkPolicy.REPLACE, req)
    }

    override fun cancelAllWork() {
        workManager.cancelAllWork()
    }
}
