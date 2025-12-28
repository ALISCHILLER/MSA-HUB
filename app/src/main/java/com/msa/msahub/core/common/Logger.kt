package com.msa.msahub.core.common

import timber.log.Timber

interface Logger {
    fun d(message: String, vararg args: Any?)
    fun e(t: Throwable?, message: String, vararg args: Any?)
    fun i(message: String, vararg args: Any?)
}

class AppLogger : Logger {
    override fun d(message: String, vararg args: Any?) = Timber.d(message, *args)
    override fun e(t: Throwable?, message: String, vararg args: Any?) = Timber.e(t, message, *args)
    override fun i(message: String, vararg args: Any?) = Timber.i(message, *args)
}
