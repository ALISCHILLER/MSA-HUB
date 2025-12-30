package com.msa.msahub.core.platform.backoff

import android.content.Context
import kotlin.math.min

class AndroidDeviceBackoffStore(
    context: Context
) : DeviceBackoffStore {

    private val prefs = context.getSharedPreferences("device_backoff", Context.MODE_PRIVATE)

    private fun kAttempts(deviceId: String) = "attempts_$deviceId"
    private fun kNext(deviceId: String) = "next_$deviceId"

    override fun nextAllowedAt(deviceId: String): Long =
        prefs.getLong(kNext(deviceId), 0L)

    override fun recordSuccess(deviceId: String) {
        prefs.edit()
            .remove(kAttempts(deviceId))
            .remove(kNext(deviceId))
            .apply()
    }

    override fun recordFailure(deviceId: String, baseDelayMs: Long, maxDelayMs: Long) {
        val attempts = prefs.getInt(kAttempts(deviceId), 0) + 1
        val delay = min(maxDelayMs, baseDelayMs * (1L shl min(attempts, 10))) // exponential
        val now = System.currentTimeMillis()
        val next = now + delay

        prefs.edit()
            .putInt(kAttempts(deviceId), attempts)
            .putLong(kNext(deviceId), next)
            .apply()
    }
}
