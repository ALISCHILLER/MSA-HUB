package com.msa.msahub.core.platform.backoff

interface DeviceBackoffStore {
    fun nextAllowedAt(deviceId: String): Long
    fun recordSuccess(deviceId: String)
    fun recordFailure(deviceId: String, baseDelayMs: Long = 2_000L, maxDelayMs: Long = 60_000L)
}
