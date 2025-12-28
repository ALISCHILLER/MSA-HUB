package com.msa.msahub.core.common

sealed class AppError {
    object Network : AppError()
    object Mqtt : AppError()
    object Database : AppError()
    object Unauthorized : AppError()
    object Validation : AppError()
    data class Unknown(val message: String? = null) : AppError()
}
