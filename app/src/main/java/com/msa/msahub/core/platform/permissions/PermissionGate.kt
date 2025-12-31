package com.msa.msahub.core.platform.permissions

import kotlinx.coroutines.flow.StateFlow

interface PermissionGate {
    fun getPermissionState(permission: String): StateFlow<PermissionState>
    fun requestPermission(permission: String)
}
