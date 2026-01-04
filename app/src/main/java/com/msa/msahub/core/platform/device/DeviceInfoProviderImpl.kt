package com.msa.msahub.core.platform.device

import android.os.Build

class DeviceInfoProviderImpl : DeviceInfoProvider {
    override fun getDeviceId(): String {
        // Simple implementation for now, should ideally be more robust
        return "${Build.MANUFACTURER}_${Build.MODEL}_${Build.SERIAL.takeIf { it != Build.UNKNOWN } ?: "ID"}"
    }

    override fun getDeviceModel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"

    override fun getOsVersion(): String = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
}
