package com.msa.msahub.core.di

import com.msa.msahub.core.common.JsonProvider
import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.platform.network.ConnectivityObserver
import com.msa.msahub.core.platform.network.NetworkConnectivityObserver
import com.msa.msahub.core.platform.network.http.KtorClientFactory
import com.msa.msahub.core.platform.network.http.NetworkConfig
import com.msa.msahub.core.platform.network.mqtt.*
import com.msa.msahub.core.platform.network.mqtt.impl.HiveMqttClientImpl
import com.msa.msahub.features.devices.data.remote.mqtt.MqttIngestor
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

object NetworkModule {
    val module = module {
        // HTTP
        single { NetworkConfig(baseUrl = "https://api.msahub.com") }
        single { JsonProvider.json }
        single { KtorClientFactory(get(), get()) }
        single<HttpClient> { get<KtorClientFactory>().create(get()) }

        // Connectivity
        single<ConnectivityObserver> { NetworkConnectivityObserver(androidContext()) }

        // MQTT
        single {
            MqttConfig(
                host = "broker.msahub.com",
                port = 1883,
                clientId = "msahub_android_${System.currentTimeMillis()}"
            )
        }
        single<MqttClient> { HiveMqttClientImpl() }

        // Connection Manager
        single {
            MqttConnectionManager(
                mqttClient = get(),
                config = get(),
                connectivityObserver = get(),
                scope = get(named("app_scope")),
                logger = get()
            )
        }

        // MQTT Ingestor
        single {
            MqttIngestor(
                mqttClient = get(),
                deviceStateDao = get(),
                ids = get(),
                scope = get(named("app_scope")),
                logger = get()
            )
        }
    }
}
