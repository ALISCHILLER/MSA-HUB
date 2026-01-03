package com.msa.msahub.core.platform.device

import android.os.Build

class DeviceInfoProviderImpl : DeviceInfoProvider {
    override fun manufacturer(): String = Build.MANUFACTURER ?: "unknown"
    override fun model(): String = Build.MODEL ?: "unknown"
    override fun androidVersion(): String = Build.VERSION.RELEASE ?: "unknown"
    override fun sdkInt(): Int = Build.VERSION.SDK_INT
}
