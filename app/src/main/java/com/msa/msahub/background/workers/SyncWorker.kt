package com.msa.msahub.background.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.msa.msahub.core.common.Logger
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * مسئول همگام‌سازی کامل داده‌ها:
 * ۱. دریافت آخرین لیست دستگاه‌ها
 * ۲. همگام‌سازی دستورات معلق (Outbox)
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val deviceRepository: DeviceRepository by inject()
    private val logger: Logger by inject()

    override suspend fun doWork(): Result {
        logger.i("SyncWorker: Starting full synchronization...")
        
        return try {
            // ۱. تلاش برای ارسال دستورات آفلاین (Outbox)
            deviceRepository.flushOutbox(max = 50)
            
            // ۲. دریافت جدیدترین وضعیت دستگاه‌ها از سرور
            deviceRepository.getDevices(forceRefresh = true)
            
            logger.i("SyncWorker: Synchronization completed successfully.")
            Result.success()
        } catch (e: Exception) {
            logger.e("SyncWorker: Sync failed", e)
            Result.retry()
        }
    }
}
