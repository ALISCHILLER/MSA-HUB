package com.msa.msahub.background.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.common.Result as AppResult
import com.msa.msahub.features.devices.domain.usecase.FlushOfflineCommandsUseCase
import org.koin.core.context.GlobalContext

class OfflineOutboxWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val logger: Logger by lazy { GlobalContext.get().get() }
    private val flushUseCase: FlushOfflineCommandsUseCase by lazy { GlobalContext.get().get() }

    override suspend fun doWork(): Result {
        val max = inputData.getInt(KEY_MAX, 50)

        logger.d("OfflineOutboxWorker: flush start (max=$max)")

        return when (val r = flushUseCase(max)) {
            is AppResult.Success -> {
                logger.i("OfflineOutboxWorker: flush success, sent=${r.data}")
                Result.success()
            }
            is AppResult.Failure -> {
                logger.w("OfflineOutboxWorker: flush failed: ${r.error.message}")
                Result.retry()
            }
        }
    }

    companion object {
        const val KEY_MAX = "max"
    }
}
