package com.msa.msahub.background.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.msa.msahub.core.common.Logger
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val deviceRepository: DeviceRepository by inject()
    private val logger: Logger by inject()

    override suspend fun doWork(): Result {
        logger.i("SyncWorker: Starting full synchronization...")
        
        val flushResult = deviceRepository.flushOutbox(max = 50)
        val flushWorkerResult = WorkerResultMapper.map(flushResult)
        if (flushWorkerResult != androidx.work.ListenableWorker.Result.success()) {
            return flushWorkerResult
        }

        val syncResult = deviceRepository.syncDevices()
        val finalResult = WorkerResultMapper.map(syncResult)
        
        if (finalResult == androidx.work.ListenableWorker.Result.success()) {
            logger.i("SyncWorker: Synchronization completed successfully.")
        } else {
            logger.e("SyncWorker: Synchronization failed")
        }
        
        return finalResult
    }
}
