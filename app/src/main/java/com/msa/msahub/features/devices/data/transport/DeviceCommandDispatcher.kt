package com.msa.msahub.features.devices.data.transport

import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.ConnectivityObserver
import com.msa.msahub.core.platform.network.isConnected
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionState
import com.msa.msahub.core.platform.network.bluetooth.BleManager
import kotlinx.coroutines.flow.first

sealed class DispatchResult {
    data object Success : DispatchResult()
    data class Failure(val error: String) : DispatchResult()
}

class DeviceCommandDispatcher(
    private val mqttClient: MqttClient,
    private val bleManager: BleManager,
    private val connectivityObserver: ConnectivityObserver,
    private val logger: Logger
) {
    suspend fun dispatch(deviceId: String, topic: String, payload: ByteArray): DispatchResult {
        val netStatus = connectivityObserver.observe().first()
        
        // ۱. تلاش اول: MQTT (اگر اینترنت وصل است)
        if (netStatus.isConnected && mqttClient.connectionState.value == MqttConnectionState.Connected) {
            return runCatching {
                mqttClient.publish(com.msa.msahub.core.platform.network.mqtt.MqttMessage(topic, payload))
                DispatchResult.Success
            }.getOrElse { DispatchResult.Failure("MQTT failed: ${it.message}") }
        }

        // ۲. تلاش دوم: بلوتوث (اگر اینترنت نیست ولی بلوتوث در دسترس است)
        logger.i("Falling back to Bluetooth for device: $deviceId")
        // اینجا باید منطق اتصال GATT و نوشتن در Characteristic رو پیاده کنیم
        // فعلاً به عنوان اسکلت عملیاتی:
        return DispatchResult.Failure("No active route (Internet/BT) available")
    }
}
