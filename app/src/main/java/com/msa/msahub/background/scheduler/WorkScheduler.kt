package com.msa.msahub.background.scheduler

interface WorkScheduler {
    fun schedulePeriodicSync()
    fun scheduleOneTimeSync()
    fun scheduleOfflineOutbox()
    fun cancelAllWork()
    fun scheduleDataCleanup()
    fun scheduleConnectionHealthCheck()
    fun scheduleAnalyticsUpload()
}
