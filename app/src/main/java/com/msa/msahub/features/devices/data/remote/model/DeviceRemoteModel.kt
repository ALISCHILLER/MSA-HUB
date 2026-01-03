package com.msa.msahub.features.devices.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceRemoteModel(
    val id: String,
    val name: String,
    val type: String,
    val capabilities: List<String>,
    val room: String? = null,
    val lastSeen: Long = 0L
)
