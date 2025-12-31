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
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // ترجیحاً وای‌فای برای صرفه‌جویی در دیتای موبایل و باتری
            .setRequiresBatteryNotLow(true)
            .build()

        val req = PeriodicWorkRequestBuilder<PeriodicSyncWorker>(1, TimeUnit.HOURS) // افزایش فاصله زمانی به ۱ ساعت برای بهینه‌سازی
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
            .addTag(WorkTags.SYNC)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkNames.PERIODIC_SYNC,
            ExistingPeriodicWorkPolicy.UPDATE, // استفاده از UPDATE برای اعمال تغییرات بهینه‌سازی
            req
        )
    }

    override fun scheduleOneTimeSync() {
        val req = OneTimeWorkRequestBuilder<SyncWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        workManager.enqueueUniqueWork("ONE_TIME_SYNC", ExistingWorkPolicy.REPLACE, req)
    }

    override fun scheduleOfflineOutbox() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val req = PeriodicWorkRequestBuilder<OfflineOutboxWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
            .addTag(WorkTags.OFFLINE_OUTBOX)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkNames.OFFLINE_OUTBOX_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            req
        )
    }

    override fun scheduleDataCleanup() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true) // فقط در حال شارژ
            .setRequiresDeviceIdle(true) // فقط وقتی کاربر با گوشی کار نمی‌کند
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
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val req = PeriodicWorkRequestBuilder<ConnectionHealthWorker>(30, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkNames.CONNECTION_HEALTH,
            ExistingPeriodicWorkPolicy.UPDATE,
            req
        )
    }

    override fun scheduleAnalyticsUpload() {
        // تحلیل‌ها معمولاً سنگین هستند، فقط در شب و روی شارژ
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        // پیاده‌سازی در صورت نیاز
    }

    override fun cancelAllWork() {
        workManager.cancelAllWork()
    }
}
