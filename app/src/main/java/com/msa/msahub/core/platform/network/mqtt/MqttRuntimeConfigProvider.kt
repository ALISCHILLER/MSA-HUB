package com.msa.msahub.core.platform.network.mqtt

import com.msa.msahub.core.platform.config.AppConfigStore
import com.msa.msahub.core.platform.config.MqttRuntimeConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

interface MqttRuntimeConfigProvider {
    val config: StateFlow<MqttRuntimeConfig>
    fun current(): MqttRuntimeConfig
}

class DataStoreMqttRuntimeConfigProvider(
    private val store: AppConfigStore,
    scope: CoroutineScope
) : MqttRuntimeConfigProvider {

    override val config: StateFlow<MqttRuntimeConfig> = store.observe()
        .map { it.mqtt }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = AppConfigStore.defaultRuntimeConfig().mqtt
        )

    override fun current(): MqttRuntimeConfig = config.value
}
