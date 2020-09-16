package com.rainist.collectcard.common.service

import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.dto.UserSyncStatusResponse

interface UserSyncStatusService {
    fun getUserSyncStatusLastCheckAt(banksaladUserId: Long, organizationId: String, transactionId: String): Long?
    fun upsertUserSyncStatus(banksaladUserId: Long, organizationId: String, transactionId: String, lastCheckAt: Long, isAllResponseOK: Boolean)

    fun getUserSyncStatus(executionContext: CollectExecutionContext): UserSyncStatusResponse
    fun updateDeleteFlagByUserIdAndCompanyId(executionContext: CollectExecutionContext)
    fun updateDeleteFlagByUserId(banksaladUserId: Long)
}
