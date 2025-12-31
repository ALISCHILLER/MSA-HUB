package com.msa.msahub.core.di

import com.msa.msahub.core.common.JsonProvider
import com.msa.msahub.core.platform.config.AppConfigStore
import com.msa.msahub.core.platform.network.ConnectivityObserver
import com.msa.msahub.core.platform.network.NetworkConnectivityObserver
import com.msa.msahub.core.platform.network.http.KtorClientFactory
import com.msa.msahub.core.platform.network.mqtt.*
import com.msa.msahub.core.platform.network.mqtt.impl.HiveMqttClientImpl
import com.msa.msahub.features.devices.data.remote.mqtt.MqttIngestor
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

object NetworkModule {
    val module = module {
        
        single { AppConfigStore(androidContext()) }
        single { JsonProvider.json }
        
        single { 
            KtorClientFactory(
                json = get(),
                authTokenStore = get(),
                baseUrlProvider = { "https://api.msa-hub.com/v1/" }
            ) 
        }
        
        single<HttpClient> { get<KtorClientFactory>().create(get()) }

        single<ConnectivityObserver> { NetworkConnectivityObserver(androidContext()) }

        single<MqttClient> { HiveMqttClientImpl() }

        single {
            MqttConnectionManager(
                mqttClient = get(),
                configProvider = {
                    // استفاده از runBlocking برای گرفتن اولین کانفیگ از دیتای استور (غیر سسپند)
                    val runtime = runBlocking { get<AppConfigStore>().observe().first() }
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
                logger = get()
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
