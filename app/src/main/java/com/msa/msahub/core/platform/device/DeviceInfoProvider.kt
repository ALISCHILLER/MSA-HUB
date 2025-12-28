package com.msa.msahub.core.platform.device

interface DeviceInfoProvider {
    fun getDeviceId(): String
    fun getDeviceModel(): String
    fun getOsVersion(): String
}
