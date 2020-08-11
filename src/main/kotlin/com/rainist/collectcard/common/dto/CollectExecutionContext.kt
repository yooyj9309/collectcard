package com.rainist.collectcard.common.dto

import com.rainist.collect.common.execution.ExecutionContext
import java.time.LocalDateTime

class CollectExecutionContext(
    private val organizationId: String,
    private val userId: String,
    private val startAt: LocalDateTime
) : ExecutionContext {

    override fun getOrganizationId(): String {
        return organizationId
    }

    override fun getUserId(): String {
        return userId
    }

    override fun getStartAt(): LocalDateTime {
        return startAt
    }
}
