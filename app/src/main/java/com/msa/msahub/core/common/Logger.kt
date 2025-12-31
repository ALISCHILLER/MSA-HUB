package com.msa.msahub.core.common

import timber.log.Timber

interface Logger {
    fun d(message: String)
    fun i(message: String)
    fun w(message: String)
    fun e(message: String, throwable: Throwable? = null)
}

class TimberLogger : Logger {
    override fun d(message: String) = Timber.d(message)
    override fun i(message: String) = Timber.i(message)
    override fun w(message: String) = Timber.w(message)
    override fun e(message: String, throwable: Throwable?) {
        if (throwable == null) Timber.e(message) else Timber.e(throwable, message)
    }
}
