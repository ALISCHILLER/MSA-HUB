package com.msa.msahub.core.platform.network.http

import com.msa.msahub.core.security.auth.AuthTokenStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * کارخانه ساخت کلاینت HTTP با پشتیبانی از احراز هویت خودکار و لاگ‌گیری
 */
class KtorClientFactory(
    private val json: Json,
    private val authTokenStore: AuthTokenStore
) {
    fun create(config: NetworkConfig): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(json)
            }
            
            install(Logging) {
                level = LogLevel.INFO
            }

            // تنظیم خودکار بیس آدرس و هدر احراز هویت
            defaultRequest {
                url(config.baseUrl)
                val token = authTokenStore.getTokenSync() // متد کمکی برای دریافت توکن
                if (!token.isNullOrBlank()) {
                    header("Authorization", "Bearer $token")
                }
            }
        }
    }
}
