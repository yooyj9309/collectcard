package com.rainist.collectcard.common.service

interface UserSyncStatusService {
    fun getUserSyncStatusLastCheckAt(banksaladUserId: Long, organizationId: String, transactionId: String): Long
    fun updateUserSyncStatus(banksaladUserId: Long, organizationId: String, transactionId: String, lastCheckAt: Long)
}
