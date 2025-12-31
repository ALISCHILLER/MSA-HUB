package com.msa.msahub.core.observability

import timber.log.Timber

interface EventLogger {
    fun logEvent(name: String, properties: Map<String, Any?> = emptyMap())
    fun logError(name: String, throwable: Throwable?, properties: Map<String, Any?> = emptyMap())
}

class TimberEventLogger : EventLogger {
    override fun logEvent(name: String, properties: Map<String, Any?>) {
        val propsString = properties.entries.joinToString { "${it.key}=${it.value}" }
        Timber.i("EVENT: [$name] $propsString")
    }

    override fun logError(name: String, throwable: Throwable?, properties: Map<String, Any?>) {
        val propsString = properties.entries.joinToString { "${it.key}=${it.value}" }
        Timber.e(throwable, "ERROR_EVENT: [$name] $propsString")
    }
}
