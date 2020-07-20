package com.rainist.collectcard.common.service

import com.rainist.collectcard.common.db.entity.UserSyncStatusEntity
import com.rainist.collectcard.common.db.repository.UserSyncStatusRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.springframework.stereotype.Service

@Service
class UserSyncStatusServiceImpl(
    private val userSyncStatusRepository: UserSyncStatusRepository
) : UserSyncStatusService {

    companion object {
        val ZONE_ID_UTC = "UTC"
    }

    override fun getUserSyncStatusLastCheckAt(
        banksaladUserId: Long,
        organizationId: String,
        transactionId: String
    ): Long {
        val userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionId(
            banksaladUserId,
            organizationId,
            transactionId
        )

        return userSyncStatusEntity?.lastCheckAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: 0
    }

    override fun updateUserSyncStatus(
        banksaladUserId: Long,
        organizationId: String,
        transactionId: String,
        lastCheckAt: Long
    ) {
        /* user_sync_status 조회 없으면 생성 */
        var userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionId(
            banksaladUserId,
            organizationId,
            transactionId
        ) ?: UserSyncStatusEntity().apply {
            this.banksaladUserId = banksaladUserId
            this.organizationId = organizationId
            this.transactionId = transactionId
        }

        /* last_check_at 업데이트 */
        userSyncStatusEntity.lastCheckAt =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(lastCheckAt), ZoneId.of(ZONE_ID_UTC))

        userSyncStatusRepository.save(userSyncStatusEntity)
    }
}
