package com.msa.msahub.features.devices.domain.model

object DeviceOnlinePolicy {
    const val STALE_MS: Long = 90_000L

    fun isEffectivelyOnline(isOnlineFlag: Boolean, lastSeenAt: Long?, now: Long): Boolean {
        if (!isOnlineFlag) return false
        if (lastSeenAt == null) return false
        return (now - lastSeenAt) <= STALE_MS
    }
}
