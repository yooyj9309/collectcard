package com.rainist.collectcard.common.service

import com.rainist.collectcard.common.db.entity.UserSyncStatusEntity
import com.rainist.collectcard.common.db.repository.UserSyncStatusRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.dto.SyncStatusResponse
import com.rainist.collectcard.common.dto.UserSyncStatusResponse
import com.rainist.common.util.DateTimeUtil
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserSyncStatusServiceImpl(
    val userSyncStatusRepository: UserSyncStatusRepository,
    val organizationService: OrganizationService
) : UserSyncStatusService {

    companion object {
        val ZONE_ID_UTC = "UTC"
    }

    override fun getUserSyncStatusLastCheckAt(
        banksaladUserId: Long,
        organizationId: String,
        transactionId: String
    ): Long? {
        val userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionIdAndIsDeleted(
            banksaladUserId,
            organizationId,
            transactionId,
            false
        )

        return userSyncStatusEntity?.lastCheckAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
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
        userSyncStatusEntity.lastCheckAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastCheckAt), ZoneId.of(ZONE_ID_UTC))
        userSyncStatusEntity.isDeleted = false

        userSyncStatusRepository.save(userSyncStatusEntity)
    }

    override fun getUserSyncStatus(executionContext: CollectExecutionContext): UserSyncStatusResponse {
        val banksaladUserId = executionContext.userId.toLong()
        val entities: List<UserSyncStatusEntity>? = if (StringUtils.isEmpty(executionContext.organizationId)) {
            userSyncStatusRepository.findByBanksaladUserIdAndIsDeleted(banksaladUserId, false)
        } else {
            userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndIsDeleted(banksaladUserId, executionContext.organizationId, false)
        }

        val userSyncStatusResponse = entities?.groupBy { it.organizationId }
            ?.map { entry ->
                SyncStatusResponse().apply {
                    this.userId = banksaladUserId
                    this.companyId = organizationService.getOrganizationByOrganizationId(entry.key.toString()).organizationObjectId
                    this.syncedAt = entry.value.map { entity ->
                        entity.lastCheckAt?.let { DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(it) } ?: 0L
                    }.max()
                }
            }

        return UserSyncStatusResponse().apply {
            dataBody = userSyncStatusResponse
        }
    }

    @Transactional
    override fun updateDeleteFlagByUserIdAndCompanyId(executionContext: CollectExecutionContext) {
        userSyncStatusRepository.updateIsDeletedByBanksaladUserIdAndOrganizationId(executionContext.userId.toLong(), executionContext.organizationId)
    }

    @Transactional
    override fun updateDeleteFlagByUserId(banksaladUserId: Long) {
        userSyncStatusRepository.updateIsDeletedByBanksaladUserId(banksaladUserId)
    }
}
