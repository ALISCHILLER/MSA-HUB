package com.msa.msahub.core.platform.network.http

import com.msa.msahub.BuildConfig
import com.msa.msahub.core.security.auth.AuthTokenStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * HttpClient with safe logging + non-blocking auth token.
 * baseUrlProvider is sync to be usable inside defaultRequest.
 */
class KtorClientFactory(
    private val json: Json,
    private val authTokenStore: AuthTokenStore,
    private val baseUrlProvider: () -> String
) {
    fun create(config: NetworkConfig): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) { json(json) }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    level = LogLevel.INFO
                    sanitizeHeader { header -> header.equals(HttpHeaders.Authorization, ignoreCase = true) }
                }
            }

            defaultRequest {
                url(baseUrlProvider())
                val token = authTokenStore.peekToken()
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }
}
