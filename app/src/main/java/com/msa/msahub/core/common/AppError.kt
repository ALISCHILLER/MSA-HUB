package com.msa.msahub.core.common

sealed class AppError : Exception() {
    data class Network(override val message: String, val code: Int? = null) : AppError()
    data class Mqtt(override val message: String, val causeThrowable: Throwable? = null) : AppError()
    data class Database(override val message: String) : AppError()
    data class Security(override val message: String) : AppError()
    data class Validation(override val message: String) : AppError()
    data class Unknown(override val message: String, val causeThrowable: Throwable? = null) : AppError()
}

/**
 * تبدیل Exceptionهای عمومی به AppError برای یکپارچگی در کل پروژه
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        is AppError -> this
        is java.net.UnknownHostException -> AppError.Network("عدم دسترسی به اینترنت")
        is java.net.SocketTimeoutException -> AppError.Network("زمان اتصال به پایان رسید")
        else -> AppError.Unknown(this.message ?: "خطای ناشناخته", this)
    }
}
