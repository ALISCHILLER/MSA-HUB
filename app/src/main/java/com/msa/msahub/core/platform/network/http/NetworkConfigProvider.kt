package com.msa.msahub.core.platform.network.http

import com.msa.msahub.core.platform.config.AppConfigStore
import com.msa.msahub.core.platform.config.HttpRuntimeConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

interface NetworkConfigProvider {
    val config: StateFlow<HttpRuntimeConfig>
    fun current(): HttpRuntimeConfig
}

class DataStoreNetworkConfigProvider(
    private val store: AppConfigStore,
    scope: CoroutineScope
) : NetworkConfigProvider {

    override val config: StateFlow<HttpRuntimeConfig> = store.observe()
        .map { it.http }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = AppConfigStore.defaultRuntimeConfig().http
        )

    override fun current(): HttpRuntimeConfig = config.value
}
