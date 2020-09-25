package com.rainist.collectcard.common.service

import java.util.UUID
import org.springframework.stereotype.Service

@Service
class UuidService {

    fun generateExecutionRequestId(): String {
        return UUID.randomUUID().toString()
    }
}
