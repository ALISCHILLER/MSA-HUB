package com.msa.msahub.core.platform.device

import android.content.Context
import android.os.BatteryManager

class BatteryStatusProviderImpl(
    private val context: Context
) : BatteryStatusProvider {

    private val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    override fun batteryPercent(): Int? {
        val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return if (level in 0..100) level else null
    }

    override fun isCharging(): Boolean? {
        val status = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        // این property همیشه reliable نیست روی همه برندها؛ اگر صفر بود null می‌دیم
        return when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING,
            BatteryManager.BATTERY_STATUS_FULL -> true
            BatteryManager.BATTERY_STATUS_DISCHARGING,
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> false
            else -> null
        }
    }
}
