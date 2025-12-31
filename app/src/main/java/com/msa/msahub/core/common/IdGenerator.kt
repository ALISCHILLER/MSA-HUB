package com.msa.msahub.core.common

import java.util.UUID

interface IdGenerator {
    fun uuid(): String
    fun generate(): String = uuid()
}

class UUIDGenerator : IdGenerator {
    override fun uuid(): String = UUID.randomUUID().toString()
}
