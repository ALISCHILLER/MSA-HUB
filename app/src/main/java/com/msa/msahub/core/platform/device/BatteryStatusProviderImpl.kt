package com.msa.msahub.core.platform.device

import android.content.Context
import android.os.BatteryManager

class BatteryStatusProviderImpl(
    private val context: Context
) : BatteryStatusProvider {

    private val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    override fun getBatteryLevel(): Int {
        val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return if (level in 0..100) level else 0
    }

    override fun isCharging(): Boolean {
        val status = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        return status == BatteryManager.BATTERY_STATUS_CHARGING || 
               status == BatteryManager.BATTERY_STATUS_FULL
    }
}
