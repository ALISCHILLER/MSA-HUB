package com.msa.msahub.core.common

interface RetryPolicy {
    fun shouldRetry(attempt: Int, error: AppError): Boolean
    fun getDelayMillis(attempt: Int): Long
}
