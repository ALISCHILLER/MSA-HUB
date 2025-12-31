package com.msa.msahub.background.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // TODO: later wire real sync (flush outbox, refresh cache, etc.)
        return Result.success()
    }
}
