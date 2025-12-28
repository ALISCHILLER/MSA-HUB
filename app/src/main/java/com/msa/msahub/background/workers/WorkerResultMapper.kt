package com.msa.msahub.background.workers

import androidx.work.ListenableWorker
import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Result

object WorkerResultMapper {

    fun <T> toWorkerResult(result: Result<T>): ListenableWorker.Result {
        return when (result) {
            is Result.Success -> ListenableWorker.Result.success()
            is Result.Failure -> mapFailure(result.error)
        }
    }

    private fun mapFailure(error: AppError): ListenableWorker.Result {
        return when (error) {
            AppError.Network -> ListenableWorker.Result.retry()
            AppError.Mqtt -> ListenableWorker.Result.retry()
            AppError.Database -> ListenableWorker.Result.failure()
            AppError.Unauthorized -> ListenableWorker.Result.failure()
            AppError.Validation -> ListenableWorker.Result.failure()
            is AppError.Unknown -> ListenableWorker.Result.failure()
        }
    }
}
