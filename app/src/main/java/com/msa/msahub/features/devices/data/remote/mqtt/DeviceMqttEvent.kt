package com.msa.msahub.features.devices.data.remote.mqtt

import com.msa.msahub.core.common.JsonProvider
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

@Serializable
data class DeviceStatusEvent(
    val deviceId: String,
    val online: Boolean,
    val state: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromJson(json: String): DeviceStatusEvent? {
            return try {
                JsonProvider.json.decodeFromString(json)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    fun toJson(): String = JsonProvider.json.encodeToString(this)
}

@Serializable
data class DeviceAckEvent(
    val deviceId: String,
    val commandId: String? = null,
    val action: String,
    val success: Boolean,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromJson(json: String): DeviceAckEvent? {
            return try {
                JsonProvider.json.decodeFromString(json)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    fun toJson(): String = JsonProvider.json.encodeToString(this)
}
