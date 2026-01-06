package com.msa.msahub.core.di

import com.msa.msahub.core.common.JsonProvider
import com.msa.msahub.core.platform.config.AppConfigStore
import com.msa.msahub.core.platform.network.ConnectivityObserver
import com.msa.msahub.core.platform.network.LocalDiscoveryManager
import com.msa.msahub.core.platform.network.NetworkConnectivityObserver
import com.msa.msahub.core.platform.network.http.DataStoreNetworkConfigProvider
import com.msa.msahub.core.platform.network.http.KtorClientFactory
import com.msa.msahub.core.platform.network.http.NetworkConfig
import com.msa.msahub.core.platform.network.http.NetworkConfigProvider
import com.msa.msahub.core.platform.network.mqtt.*
import com.msa.msahub.core.platform.network.mqtt.impl.HiveMqttClientImpl
import com.msa.msahub.features.devices.data.remote.mqtt.MqttIngestor
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

object NetworkModule {
    val module = module {

        single { AppConfigStore(androidContext(), get()) }
        single { JsonProvider.json }

        single<NetworkConfigProvider> {
            DataStoreNetworkConfigProvider(
                store = get(),
                scope = get(named("app_scope"))
            )
        }

        single<MqttRuntimeConfigProvider> {
            DataStoreMqttRuntimeConfigProvider(
                store = get(),
                scope = get(named("app_scope"))
            )
        }

        single { TlsProvider() }

        single {
            KtorClientFactory(
                json = get(),
                authTokenStore = get(),
                baseUrlProvider = { get<NetworkConfigProvider>().current().baseUrl }
            )
        }

        single<HttpClient> { 
            val cfg = get<NetworkConfigProvider>().current()
            get<KtorClientFactory>().create(NetworkConfig(cfg.baseUrl, cfg.connectTimeoutMs, cfg.requestTimeoutMs)) 
        }

        single<ConnectivityObserver> { NetworkConnectivityObserver(androidContext()) }

        single<MqttClient> { HiveMqttClientImpl() }

        single {
            MqttConnectionManager(
                mqttClient = get(),
                runtimeConfigProvider = get(),
                tlsProvider = get(),
                connectivityObserver = get(),
                scope = get(named("app_scope")),
                logger = get()
            )
        }

        single {
            MqttIngestor(
                mqttClient = get(),
                deviceStateDao = get(),
                deviceHistoryDao = get(),
                ids = get(),
                scope = get(named("app_scope")),
                logger = get()
            )
        }

        single { LocalDiscoveryManager(androidContext(), get()) }
    }
}
