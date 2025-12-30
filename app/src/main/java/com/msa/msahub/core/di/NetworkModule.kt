package com.msa.msahub.core.di

import com.msa.msahub.core.platform.network.http.HttpClientProvider
import com.msa.msahub.core.platform.network.http.NetworkConfig
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttConfig
import com.msa.msahub.core.platform.network.mqtt.impl.HiveMqttClientImpl
import io.ktor.client.HttpClient
import org.koin.dsl.module

object NetworkModule {
    val module = module {
        // HTTP Configuration
        single { NetworkConfig(baseUrl = "https://api.msahub.com") } // آدرس فرضی API
        single<HttpClient> { HttpClientProvider.provide(get()) }

        // MQTT Configuration
        single { 
            MqttConfig(
                host = "broker.msahub.com", // آدرس بروکر هاب
                port = 1883,
                clientId = "msahub_android_${System.currentTimeMillis()}"
            )
        }
        
        // ثبت پیاده‌سازی واقعی MQTT
        single<MqttClient> { HiveMqttClientImpl() }
    }
}
