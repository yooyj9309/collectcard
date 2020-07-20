package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.UserSyncStatusEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserSyncStatusRepository : JpaRepository<UserSyncStatusEntity, Long> {
    fun findByBanksaladUserIdAndOrganizationIdAndTransactionId(
        banksaladUserId: Long,
        organizationId: String,
        transactionId: String
    ): UserSyncStatusEntity?
}
