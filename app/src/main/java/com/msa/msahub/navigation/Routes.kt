package com.msa.msahub.navigation

object Routes {
    // Top-level destinations
    const val HOME = "home"
    const val DEVICES = "devices"
    const val SCENES = "scenes"
    const val SETTINGS = "settings"

    // Device nested routes
    const val DEVICE_DETAIL = "devices/{deviceId}"
    const val DEVICE_HISTORY = "devices/{deviceId}/history"
    const val DEVICE_SETTINGS = "devices/{deviceId}/settings"

    // Helper functions for dynamic routes
    fun deviceDetail(id: String) = "devices/$id"
    fun deviceHistory(id: String) = "devices/$id/history"
    fun deviceSettings(id: String) = "devices/$id/settings"
}
