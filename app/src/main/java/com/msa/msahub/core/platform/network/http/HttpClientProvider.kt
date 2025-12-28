package com.msa.msahub.core.platform.network.http

import io.ktor.client.HttpClient

interface HttpClientProvider {
    fun getClient(): HttpClient
}
