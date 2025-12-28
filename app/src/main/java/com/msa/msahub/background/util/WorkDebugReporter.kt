package com.msa.msahub.background.util

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.common.util.concurrent.ListenableFuture

class WorkDebugReporter(private val workManager: WorkManager) {

    fun activeWorkSnapshot(tag: String): ListenableFuture<List<WorkInfo>> {
        return workManager.getWorkInfosByTag(tag)
    }

    fun stateLabel(state: WorkInfo.State): String {
        return when (state) {
            WorkInfo.State.ENQUEUED -> "enqueued"
            WorkInfo.State.RUNNING -> "running"
            WorkInfo.State.SUCCEEDED -> "succeeded"
            WorkInfo.State.FAILED -> "failed"
            WorkInfo.State.BLOCKED -> "blocked"
            WorkInfo.State.CANCELLED -> "cancelled"
        }
    }
}
