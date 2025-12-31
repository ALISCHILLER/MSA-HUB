package com.msa.msahub.background.workers

import androidx.work.ListenableWorker
import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Result

/**
 * نگاشت نتایج بیزینس به نتایج WorkManager
 */
object WorkerResultMapper {

    fun <T> map(result: Result<T>): ListenableWorker.Result {
        return when (result) {
            is Result.Success -> ListenableWorker.Result.success()
            is Result.Failure -> mapFailure(result.error)
        }
    }

    private fun mapFailure(error: AppError): ListenableWorker.Result {
        return when (error) {
            // خطاهای شبکه و MQTT معمولاً موقتی هستند و نیاز به تکرار دارند
            is AppError.Network,
            is AppError.Mqtt -> ListenableWorker.Result.retry()

            // خطاهای دیتابیس، امنیت و اعتبارسنجی معمولاً دائمی هستند
            is AppError.Database,
            is AppError.Security,
            is AppError.Validation,
            is AppError.Unknown -> ListenableWorker.Result.failure()
        }
    }
}
