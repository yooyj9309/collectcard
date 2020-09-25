package com.rainist.collectcard.common.dto

import com.rainist.collect.common.execution.ExecutionContext
import java.time.LocalDateTime

data class CollectExecutionContext(
    private var executionRequestId: String,
    private var userId: String,
    private var organizationId: String,
    private var startAt: LocalDateTime? = null
) : ExecutionContext {

    fun setStartAt(startAt: LocalDateTime?) {
        this.startAt = startAt
    }

    override fun getUserId(): String {
        return this.userId
    }

    override fun getExecutionRequestId(): String {
        return this.executionRequestId
    }

    override fun getStartAt(): LocalDateTime? {
        return this.startAt
    }

    override fun getOrganizationId(): String {
        return this.organizationId
    }
}
