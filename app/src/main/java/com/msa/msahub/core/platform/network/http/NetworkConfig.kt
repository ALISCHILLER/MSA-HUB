package com.msa.msahub.core.platform.network.http

data class NetworkConfig(
    val baseUrl: String,
    val connectTimeout: Long = 30_000,
    val requestTimeout: Long = 30_000
)
