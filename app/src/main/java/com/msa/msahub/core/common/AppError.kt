package com.msa.msahub.core.common

sealed class AppError(open val message: String, open val cause: Throwable? = null) {
    data class Network(override val message: String, override val cause: Throwable? = null) : AppError(message, cause)
    data class Mqtt(override val message: String, override val cause: Throwable? = null) : AppError(message, cause)
    data class Database(override val message: String, override val cause: Throwable? = null) : AppError(message, cause)
    data class Unauthorized(override val message: String = "Unauthorized") : AppError(message)
    data class Validation(override val message: String) : AppError(message)
    data class Unknown(override val message: String, override val cause: Throwable? = null) : AppError(message, cause)
}
