package com.msa.msahub.core.platform.device

interface BatteryStatusProvider {
    fun getBatteryLevel(): Int
    fun isCharging(): Boolean
}
