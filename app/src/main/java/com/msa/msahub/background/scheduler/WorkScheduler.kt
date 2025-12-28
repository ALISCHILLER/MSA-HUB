package com.msa.msahub.background.scheduler

interface WorkScheduler {
    fun scheduleAll()
    fun scheduleOutboxNow()
    fun scheduleOutboxPeriodic()
    fun schedulePeriodicSync()
    fun scheduleOneTimeSync()
    fun scheduleOfflineOutbox()
    fun cancelAllWork()
    fun scheduleDataCleanup()
    fun scheduleConnectionHealthCheck()
    fun scheduleAnalyticsUpload()
}
