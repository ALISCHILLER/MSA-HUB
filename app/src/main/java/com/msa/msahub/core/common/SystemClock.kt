package com.msa.msahub.core.common

class SystemClock : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}
