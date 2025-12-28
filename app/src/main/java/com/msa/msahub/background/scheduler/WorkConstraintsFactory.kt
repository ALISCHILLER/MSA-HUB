package com.msa.msahub.background.scheduler

import androidx.work.Constraints
import androidx.work.NetworkType

object WorkConstraintsFactory {

    fun anyNetwork(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    fun wifiOnly(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
    }

    fun requiresCharging(): Constraints {
        return Constraints.Builder()
            .setRequiresCharging(true)
            .build()
    }

    fun requiresDeviceIdle(): Constraints {
        return Constraints.Builder()
            .setRequiresDeviceIdle(true)
            .build()
    }

    fun batteryNotLow(): Constraints {
        return Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
    }
}
