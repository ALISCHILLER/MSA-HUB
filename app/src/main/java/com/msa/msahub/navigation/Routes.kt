package com.msa.msahub.navigation

object Routes {
    const val HOME = "home"
    const val DASHBOARD = "dashboard"
    
    const val DEVICES_LIST = "devices_list"
    const val DEVICE_DETAIL = "device_detail/{deviceId}"
    const val DEVICE_HISTORY = "device_history/{deviceId}"
    const val DEVICE_SETTINGS = "device_settings/{deviceId}"
    
    const val SCENES_LIST = "scenes_list"
    const val SCENE_EDITOR = "scene_editor/{sceneId}"
    
    const val SETTINGS = "settings"
    const val CONNECTION_MANAGER = "connection_manager"
    
    fun deviceDetail(id: String) = "device_detail/$id"
}
