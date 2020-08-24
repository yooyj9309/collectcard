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

    fun findByBanksaladUserIdAndOrganizationIdAndTransactionId(
        banksaladUserId: Long,
        organizationId: String,
        transactionId: String
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
    @Query("update UserSyncStatusEntity t set t.isDeleted = true where t.banksaladUserId = ?1 and t.organizationId = ?2")
    fun updateIsDeletedByBanksaladUserIdAndOrganizationId(
        banksaladUserId: Long,
        organizationId: String
    )

    @Modifying(clearAutomatically = true)
    @Query("update UserSyncStatusEntity t set t.isDeleted = true where t.banksaladUserId = ?1")
    fun updateIsDeletedByBanksaladUserId(
        banksaladUserId: Long
    )
}
