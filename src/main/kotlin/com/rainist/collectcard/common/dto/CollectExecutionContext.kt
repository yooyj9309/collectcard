package com.rainist.collectcard.common.dto

import com.rainist.collect.common.execution.ExecutionContext

class CollectExecutionContext(private val organizationId: String, private val userId: String) : ExecutionContext {

    override fun getOrganizationId(): String {
        return organizationId
    }

    override fun getUserId(): String {
        return userId
    }
}
