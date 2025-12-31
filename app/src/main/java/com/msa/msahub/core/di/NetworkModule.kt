package com.msa.msahub.core.di

import com.msa.msahub.core.common.JsonProvider
import com.msa.msahub.core.platform.config.AppConfigStore
import com.msa.msahub.core.platform.network.ConnectivityObserver
import com.msa.msahub.core.platform.network.NetworkConnectivityObserver
import com.msa.msahub.core.platform.network.http.KtorClientFactory
import com.msa.msahub.core.platform.network.http.NetworkConfig
import com.msa.msahub.core.platform.network.mqtt.*
import com.msa.msahub.core.platform.network.mqtt.impl.HiveMqttClientImpl
import com.msa.msahub.features.devices.data.remote.mqtt.MqttIngestor
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.first
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

object NetworkModule {
    val module = module {
        
        // تنظیمات داینامیک
        single { AppConfigStore(androidContext()) }
        
        single { JsonProvider.json }
        
        single { 
            KtorClientFactory(
                json = get(),
                authTokenStore = get(),
                baseUrlProvider = { "https://api.msa-hub.com/v1/" } // قابل انتقال به AppConfigStore
            ) 
        }
        
        single<HttpClient> { get<KtorClientFactory>().create(get()) }

        single<ConnectivityObserver> { NetworkConnectivityObserver(androidContext()) }

        // MQTT Components
        single<MqttClient> { HiveMqttClientImpl() }
        factory { BackoffPolicy() }

        single {
            MqttConnectionManager(
                mqttClient = get(),
                configProvider = {
                    val runtime = get<AppConfigStore>().observe().first()
                    val m = runtime.mqtt
                    MqttConfig(
                        host = m.host,
                        port = m.port,
                        clientId = "${m.clientIdPrefix}_${System.currentTimeMillis()}",
                        username = m.username,
                        password = m.password,
                        useTls = m.useTls,
                        keepAlive = m.keepAliveSec
                    )
                },
                connectivityObserver = get(),
                scope = get(named("app_scope")),
                logger = get(),
                backoff = get()
            )
        }

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
