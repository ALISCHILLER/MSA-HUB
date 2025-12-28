package com.msa.msahub.core.platform.network.http

import io.ktor.client.HttpClientConfig

class NetworkInterceptors {
    fun apply(config: HttpClientConfig<*>) {
        // Add auth, headers, etc.
    }
}
