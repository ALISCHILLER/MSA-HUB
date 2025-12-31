package com.msa.msahub.background.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.msa.msahub.core.common.Logger
import com.msa.msahub.features.devices.data.local.dao.OfflineCommandDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

/**
 * وظیفه پاکسازی داده‌های قدیمی و غیرضروری برای جلوگیری از رشد بی‌رویه دیتابیس.
 */
class DataCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val dao: OfflineCommandDao by inject()
    private val logger: Logger by inject()

    override suspend fun doWork(): Result {
        return try {
            logger.i("Starting data cleanup process...")

            // پاکسازی دستورات ارسال شده قدیمی (مثلاً بیش از ۳ روز)
            val threeDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)
            dao.deleteOldSentCommands(threeDaysAgo)

            // اینجا می‌توان سایر موارد پاکسازی (مانند تاریخچه سنسورها) را اضافه کرد
            
            logger.i("Data cleanup completed successfully.")
            Result.success()
        } catch (e: Exception) {
            logger.e("Data cleanup failed", e)
            Result.failure()
        }
    }
}
