package com.msa.msahub.core.common

import java.util.UUID

interface IdGenerator {
    fun generateId(): String
}

class UUIDGenerator : IdGenerator {
    override fun generateId(): String = UUID.randomUUID().toString()
}
