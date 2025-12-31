package com.msa.msahub.core.common

import java.util.UUID

interface IdGenerator {
    fun uuid(): String
}

class UUIDGenerator : IdGenerator {
    override fun uuid(): String = UUID.randomUUID().toString()
}
