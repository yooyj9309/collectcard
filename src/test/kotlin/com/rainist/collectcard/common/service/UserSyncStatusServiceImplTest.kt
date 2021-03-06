package com.rainist.collectcard.common.service

import com.rainist.collectcard.common.db.repository.UserSyncStatusRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.dto.SyncStatusResponse
import java.time.ZoneOffset
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("UserSyncStatus")
internal class UserSyncStatusServiceImplTest {

    @Autowired
    lateinit var userSyncStatusRepository: UserSyncStatusRepository

    @Autowired
    lateinit var userSyncStatusService: UserSyncStatusService

    @Autowired
    lateinit var organizationService: OrganizationService

    @Test
    @Rollback
    @Transactional
    fun upsertUserSyncStatus_insert_transaction() {
        val banksaladUserId = 1.toLong()
        val organizationId = "shinhancard"
        val transactionId = "cards"

        val lastCheckAt = System.currentTimeMillis()

        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt, true)

        val userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionIdAndIsDeleted(
            banksaladUserId,
            organizationId,
            transactionId,
            false
        )

        Assert.assertEquals(lastCheckAt, userSyncStatusEntity?.lastCheckAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())
    }

    @Test
    @Rollback
    @Transactional
    fun upsertUserSyncStatus_update_transaction() {
        val banksaladUserId = 2.toLong()
        val organizationId = "shinhancard"
        val transactionId = "cards"

        val lastCheckAt = System.currentTimeMillis()
        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt, true)

        var userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionIdAndIsDeleted(
            banksaladUserId,
            organizationId,
            transactionId,
            false
        )

        Assert.assertEquals(lastCheckAt, userSyncStatusEntity?.lastCheckAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())

        val lastCheckAt2 = System.currentTimeMillis() + 1000
        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt2, true)

        userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionIdAndIsDeleted(
            banksaladUserId,
            organizationId,
            transactionId,
            false
        )

        Assert.assertEquals(lastCheckAt2, userSyncStatusEntity?.lastCheckAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())
    }

    @Test
    @Rollback
    @Transactional
    fun getLastUserSyncStatus() {
        val banksaladUserId = 3.toLong()
        val organizationId = "shinhancard"
        val transactionId = "cards"

        var lastCheckAtFromDB =
            userSyncStatusService.getUserSyncStatusLastCheckAt(banksaladUserId, organizationId, transactionId)
        Assert.assertEquals(null, lastCheckAtFromDB)

        val lastCheckAt = System.currentTimeMillis()
        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt, true)

        lastCheckAtFromDB =
            userSyncStatusService.getUserSyncStatusLastCheckAt(banksaladUserId, organizationId, transactionId)
        Assert.assertEquals(lastCheckAt, lastCheckAtFromDB)
    }

    @Test
    @Rollback
    @Transactional
    fun getUserSyncStatus_single() {
        val banksaladUserId = 1.toLong()
        val organizationId = "shinhancard"
        val companyId = organizationService.getOrganizationByOrganizationId(organizationId).organizationObjectId
        val now = System.currentTimeMillis()
        val executionContext = CollectExecutionContext("", banksaladUserId.toString(), organizationId)

        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId, "cards", now, true)

        var res = userSyncStatusService.getUserSyncStatus(executionContext)
        var list = res.dataBody ?: mutableListOf()

        assertEquals(1, list.size)
        assertThat(list[0]).isEqualToIgnoringGivenFields(SyncStatusResponse().apply {
            this.userId = banksaladUserId
            this.syncedAt = now
            this.companyId = companyId
        })
    }

    @Test
    @Rollback
    @Transactional
    fun getUserSyncStatus_all() {
        val banksaladUserId = 1.toLong()
        val organizationId = "shinhancard"
        val companyId = organizationService.getOrganizationByOrganizationId(organizationId).organizationObjectId
        val now = System.currentTimeMillis()
        val executionContext = CollectExecutionContext("", banksaladUserId.toString(), "")

        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId, "cards", now, true)

        var res = userSyncStatusService.getUserSyncStatus(executionContext)
        var list = res.dataBody ?: mutableListOf()

        assertEquals(1, list.size)
        assertThat(list[0]).isEqualToIgnoringGivenFields(SyncStatusResponse().apply {
            this.userId = banksaladUserId
            this.syncedAt = now
            this.companyId = companyId
        })
    }

    @Test
    @Rollback
    @Transactional
    fun upsertUserSyncStatus_updateByUserIdAndCompanyId() {
        val banksaladUserId = 4.toLong()
        val organizationId = "shinhancard"
        val organizationId_kb = "kbcard"
        val executionContext = CollectExecutionContext("", banksaladUserId.toString(), organizationId_kb)

        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId, "cards", System.currentTimeMillis(), true)
        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId_kb, "cards", System.currentTimeMillis(), true)

        var userSyncStatus = userSyncStatusRepository.findAll().filter { it.banksaladUserId == banksaladUserId && it.isDeleted == false }
        assertEquals(2, userSyncStatus.size)

        userSyncStatusService.updateDeleteFlagByUserIdAndCompanyId(executionContext)

        userSyncStatus = userSyncStatusRepository.findAll().filter { it.banksaladUserId == banksaladUserId && it.isDeleted == false }
        assertEquals(1, userSyncStatus.size)
        assertEquals(organizationId, userSyncStatus[0].organizationId)
    }

    @Test
    @Rollback
    @Transactional
    fun upsertUserSyncStatus_updateByUserId() {
        val banksaladUserId = 5.toLong()
        val organizationId = "shinhancard"
        val organizationId_kb = "kbcard"

        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId, "cards", System.currentTimeMillis(), true)
        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId_kb, "cards", System.currentTimeMillis(), true)

        var userSyncStatus = userSyncStatusRepository.findAll().filter { it.banksaladUserId == banksaladUserId && it.isDeleted == false }
        Assert.assertEquals(2, userSyncStatus.size)

        userSyncStatusService.updateDeleteFlagByUserId(banksaladUserId)
        userSyncStatus = userSyncStatusRepository.findAll().filter { it.banksaladUserId == banksaladUserId && it.isDeleted == false }
        Assert.assertEquals(0, userSyncStatus.size)
    }

    @Test
    @Rollback
    @Transactional
    fun upsertUserSyncStatus_hasNotOKResponse() {
        val banksaladUserId = 1.toLong()
        val organizationId = "shinhancard"
        val transactionId = "cards"

        val lastCheckAt = System.currentTimeMillis()

        userSyncStatusService.upsertUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt, false)

        val userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionIdAndIsDeleted(
            banksaladUserId,
            organizationId,
            transactionId,
            false
        )

        Assert.assertEquals(1000L, userSyncStatusEntity?.lastCheckAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())
    }
}
