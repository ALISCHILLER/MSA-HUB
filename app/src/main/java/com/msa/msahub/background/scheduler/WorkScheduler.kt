package com.msa.msahub.background.scheduler

interface WorkScheduler {
    fun scheduleAll()
    fun scheduleOutboxNow()
    fun scheduleOfflineOutbox()
    fun schedulePeriodicSync()
    fun scheduleOneTimeSync()
    fun cancelAllWork()
    fun scheduleDataCleanup()
    fun scheduleConnectionHealthCheck()
    fun scheduleAnalyticsUpload()
}
