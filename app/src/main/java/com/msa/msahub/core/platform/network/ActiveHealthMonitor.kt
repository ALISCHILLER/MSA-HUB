package com.msa.msahub.core.platform.network

import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface SystemHealth {
    data object Healthy : SystemHealth
    data class Degraded(val message: String) : SystemHealth
    data class Critical(val error: String) : SystemHealth
}

class ActiveHealthMonitor(
    private val mqttClient: MqttClient,
    private val connectivityObserver: ConnectivityObserver,
    private val scope: CoroutineScope,
    private val logger: Logger
) {
    private val _healthState = MutableStateFlow<SystemHealth>(SystemHealth.Healthy)
    val healthState: StateFlow<SystemHealth> = _healthState.asStateFlow()

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        // ۱. پایش وضعیت شبکه و MQTT به صورت ترکیبی
        combine(
            connectivityObserver.observe(),
            mqttClient.connectionState
        ) { netStatus, mqttState ->
            when {
                !netStatus.isConnected -> SystemHealth.Critical("No Internet or Local Connection")
                mqttState is MqttConnectionState.Failed -> SystemHealth.Degraded("Cloud Gateway Unreachable")
                mqttState is MqttConnectionState.Disconnected -> SystemHealth.Degraded("Connecting to Hub...")
                else -> SystemHealth.Healthy
            }
        }.onEach { 
            _healthState.value = it
            if (it is SystemHealth.Critical) logger.e("System Health Critical: ${it.error}")
        }.launchIn(scope)

        // ۲. پایش ضربان قلب (Heartbeat) سیستم - Placeholder برای فاز بعد
        scope.launch {
            while (true) {
                delay(30000) // هر ۳۰ ثانیه
                checkDeviceStaleness()
            }
        }
    }

    private fun checkDeviceStaleness() {
        // منطق تشخیص دستگاه‌هایی که مدت زیادی پیام نفرستادن
    }
}
