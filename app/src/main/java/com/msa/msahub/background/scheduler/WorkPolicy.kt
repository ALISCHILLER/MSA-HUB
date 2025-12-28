package com.msa.msahub.background.scheduler

import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import java.time.Duration

object WorkPolicy {
    val periodicPolicy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP
    val oneTimePolicy: ExistingWorkPolicy = ExistingWorkPolicy.KEEP

    val defaultBackoffPolicy: BackoffPolicy = BackoffPolicy.EXPONENTIAL
    val defaultBackoffDelay: Duration = Duration.ofMinutes(10)
}
