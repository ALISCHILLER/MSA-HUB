package com.msa.msahub.background.scheduler

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.msa.msahub.background.workers.AnalyticsUploadWorker
import com.msa.msahub.background.workers.ConnectionHealthWorker
import com.msa.msahub.background.workers.DataCleanupWorker
import com.msa.msahub.background.workers.OfflineOutboxWorker
import com.msa.msahub.background.workers.OneTimeSyncWorker
import com.msa.msahub.background.workers.PeriodicSyncWorker
import java.time.Duration

class WorkSchedulerImpl(
    private val workManager: WorkManager
) : WorkScheduler {

    override fun schedulePeriodicSync() {
        val request = PeriodicWorkRequestBuilder<PeriodicSyncWorker>(SYNC_INTERVAL)
            .setConstraints(WorkConstraintsFactory.anyNetwork())
            .setBackoffCriteria(WorkPolicy.defaultBackoffPolicy, WorkPolicy.defaultBackoffDelay)
            .addTag(WorkTags.SYNC)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkTags.PERIODIC_SYNC_NAME,
            WorkPolicy.periodicPolicy,
            request
        )
    }

    override fun scheduleOneTimeSync() {
        enqueueOneTime(
            name = WorkTags.ONE_TIME_SYNC_NAME,
            policy = WorkPolicy.oneTimePolicy,
            constraints = WorkConstraintsFactory.anyNetwork(),
            worker = OneTimeSyncWorker::class.java,
            tag = WorkTags.SYNC
        )
    }

    override fun scheduleOfflineOutbox() {
        enqueueOneTime(
            name = WorkTags.OFFLINE_OUTBOX_NAME,
            policy = WorkPolicy.oneTimePolicy,
            constraints = WorkConstraintsFactory.anyNetwork(),
            worker = OfflineOutboxWorker::class.java,
            tag = WorkTags.SYNC
        )
    }

    override fun scheduleDataCleanup() {
        enqueueOneTime(
            name = WorkTags.DATA_CLEANUP_NAME,
            policy = ExistingWorkPolicy.KEEP,
            constraints = WorkConstraintsFactory.requiresCharging(),
            worker = DataCleanupWorker::class.java,
            tag = WorkTags.CLEANUP
        )
    }

    override fun scheduleConnectionHealthCheck() {
        val request = PeriodicWorkRequestBuilder<ConnectionHealthWorker>(HEALTH_INTERVAL)
            .setConstraints(WorkConstraintsFactory.anyNetwork())
            .setBackoffCriteria(WorkPolicy.defaultBackoffPolicy, WorkPolicy.defaultBackoffDelay)
            .addTag(WorkTags.HEALTH)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkTags.CONNECTION_HEALTH_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override fun scheduleAnalyticsUpload() {
        enqueueOneTime(
            name = WorkTags.ANALYTICS_UPLOAD_NAME,
            policy = ExistingWorkPolicy.KEEP,
            constraints = WorkConstraintsFactory.anyNetwork(),
            worker = AnalyticsUploadWorker::class.java,
            tag = WorkTags.ANALYTICS
        )
    }

    override fun cancelAllWork() {
        workManager.cancelAllWork()
    }

    private fun <T : androidx.work.ListenableWorker> enqueueOneTime(
        name: String,
        policy: ExistingWorkPolicy,
        constraints: Constraints,
        worker: Class<T>,
        tag: String
    ) {
        val request = OneTimeWorkRequest.Builder(worker)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkPolicy.defaultBackoffDelay)
            .addTag(tag)
            .build()

        workManager.enqueueUniqueWork(name, policy, request)
    }

    private companion object {
        val SYNC_INTERVAL: Duration = Duration.ofHours(6)
        val HEALTH_INTERVAL: Duration = Duration.ofHours(4)
    }
}
