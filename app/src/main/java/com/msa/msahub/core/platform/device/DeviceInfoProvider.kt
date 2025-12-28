package com.msa.msahub.core.platform.device

interface DeviceInfoProvider {
    fun getDeviceModel(): String
    fun getOsVersion(): String
    fun getAppVersion(): String
}
