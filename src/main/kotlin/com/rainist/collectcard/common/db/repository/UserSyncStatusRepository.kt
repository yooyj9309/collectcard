package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.UserSyncStatusEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface UserSyncStatusRepository : JpaRepository<UserSyncStatusEntity, Long> {
    fun findByBanksaladUserIdAndOrganizationIdAndTransactionIdAndIsDeleted(
        banksaladUserId: Long,
        organizationId: String,
        transactionId: String,
        isDeleted: Boolean
    ): UserSyncStatusEntity?

    fun findByBanksaladUserIdAndOrganizationIdAndIsDeleted(
        banksaladUserId: Long,
        organizationId: String,
        isDeleted: Boolean
    ): List<UserSyncStatusEntity>?

    fun findByBanksaladUserIdAndIsDeleted(
        banksaladUserId: Long,
        isDeleted: Boolean
    ): List<UserSyncStatusEntity>?

    @Modifying(clearAutomatically = true)
    @Query("update UserSyncStatusEntity t set t.isDeleted = true where t.banksaladUserId = ?1 and t.organizationId = ?2 and t.isDeleted = false")
    fun updateIsDeletedByBanksaladUserIdAndOrganizationId(
        banksaladUserId: Long,
        organizationId: String
    )

    @Modifying(clearAutomatically = true)
    @Query("update UserSyncStatusEntity t set t.isDeleted = true where t.banksaladUserId = ?1 and t.isDeleted = false")
    fun updateIsDeletedByBanksaladUserId(
        banksaladUserId: Long
    )
}
