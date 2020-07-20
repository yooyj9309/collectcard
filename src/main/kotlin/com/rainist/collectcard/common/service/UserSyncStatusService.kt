package com.rainist.collectcard.common.service

interface UserSyncStatusService {
    fun updateUserSyncStatus(banksaladUserId: Long, organizationId: String, transactionId: String, lastCheckAt: Long)
}
