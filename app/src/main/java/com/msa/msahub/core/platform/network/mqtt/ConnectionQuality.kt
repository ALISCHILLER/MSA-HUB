package com.msa.msahub.core.platform.network.mqtt

sealed interface ConnectionQuality {
    data object Offline : ConnectionQuality
    data object Poor : ConnectionQuality
    data object Good : ConnectionQuality
    data object Excellent : ConnectionQuality
}
