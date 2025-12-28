package com.msa.msahub.core.security.keys

class KeyRotationPolicy {
    fun shouldRotate(keyAgeDays: Int): Boolean {
        return keyAgeDays > 90 // Rotate every 90 days
    }
}
