package com.msa.msahub.core.di

import com.msa.msahub.BuildConfig
import com.msa.msahub.app.AppConfig
import com.msa.msahub.core.common.JsonProvider
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
        // HTTP Config
        single {
            val baseUrl = if (BuildConfig.DEBUG) AppConfig.API_BASE_URL_DEV else AppConfig.API_BASE_URL_PROD
            NetworkConfig(baseUrl = baseUrl)
        }
        
        single { JsonProvider.json }
        
        // Factory with Lambda for baseUrl to support dynamic changes if needed
        single { 
            KtorClientFactory(
                json = get(),
                authTokenStore = get(),
                baseUrlProvider = { get<NetworkConfig>().baseUrl }
            ) 
        }
        
        single<HttpClient> { get<KtorClientFactory>().create(get()) }

        // Connectivity
        single<ConnectivityObserver> { NetworkConnectivityObserver(androidContext()) }

        // MQTT Config (P0: Security & Hardcode removal)
        single {
            val isDebug = BuildConfig.DEBUG
            MqttConfig(
                host = if (isDebug) AppConfig.MQTT_HOST_DEV else AppConfig.MQTT_HOST_PROD,
                port = if (isDebug) 1883 else AppConfig.MQTT_PORT_TLS,
                clientId = "msahub_android_${System.currentTimeMillis()}",
                useTls = !isDebug // Enable TLS in production
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
