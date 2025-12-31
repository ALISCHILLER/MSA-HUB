package com.msa.msahub.core.platform.network.mqtt

import kotlin.math.min
import kotlin.random.Random

class BackoffPolicy(
    private val baseMs: Long = 1_000,
    private val maxMs: Long = 60_000,
    private val jitterRatio: Double = 0.2
) {
    private var attempt = 0

    fun reset() { attempt = 0 }

    fun nextDelayMs(): Long {
        val exp = baseMs * (1L shl min(attempt, 10))
        val raw = min(exp, maxMs)
        attempt++

        val jitter = (raw * jitterRatio).toLong()
        val delta = if (jitter > 0) Random.nextLong(-jitter, jitter + 1) else 0
        return (raw + delta).coerceAtLeast(0)
    }
}
