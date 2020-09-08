package com.rainist.collectcard.common.service

import com.rainist.collectcard.common.db.repository.UserSyncStatusRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import java.time.ZoneOffset
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("UserSyncStatus")
internal class UserSyncStatusServiceImplTest {

    @Autowired
    lateinit var userSyncStatusRepository: UserSyncStatusRepository

    @Autowired
    lateinit var userSyncStatusService: UserSyncStatusService

    @Test
    fun updateUserSyncStatus_insert_transaction() {
        val banksaladUserId = 1.toLong()
        val organizationId = "shinhancard"
        val transactionId = "cards"

        val lastCheckAt = System.currentTimeMillis()

        userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt)

        var userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionIdAndIsDeleted(
            banksaladUserId,
            organizationId,
            transactionId,
            false
        )

        Assert.assertEquals(lastCheckAt, userSyncStatusEntity?.lastCheckAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())
    }

    @Test
    fun updateUserSyncStatus_update_transaction() {
        val banksaladUserId = 2.toLong()
        val organizationId = "shinhancard"
        val transactionId = "cards"

        val lastCheckAt = System.currentTimeMillis()
        userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt)

        var userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionIdAndIsDeleted(
            banksaladUserId,
            organizationId,
            transactionId,
            false
        )

        Assert.assertEquals(lastCheckAt, userSyncStatusEntity?.lastCheckAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())

        val lastCheckAt2 = System.currentTimeMillis() + 1000
        userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt2)

        userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionIdAndIsDeleted(
            banksaladUserId,
            organizationId,
            transactionId,
            false
        )

        Assert.assertEquals(lastCheckAt2, userSyncStatusEntity?.lastCheckAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())
    }

    @Test
    fun getLastUserSyncStatus() {
        val banksaladUserId = 3.toLong()
        val organizationId = "shinhancard"
        val transactionId = "cards"

        var lastCheckAtFromDB =
            userSyncStatusService.getUserSyncStatusLastCheckAt(banksaladUserId, organizationId, transactionId)
        Assert.assertEquals(null, lastCheckAtFromDB)

        var lastCheckAt = System.currentTimeMillis()
        userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt)

        lastCheckAtFromDB =
            userSyncStatusService.getUserSyncStatusLastCheckAt(banksaladUserId, organizationId, transactionId)
        Assert.assertEquals(lastCheckAt, lastCheckAtFromDB)
    }

    @Test
    fun updateUserSyncStatus_updateByUserIdAndCompanyId() {
        val banksaladUserId = 4.toLong()
        val organizationId = "shinhancard"
        val organizationId_kb = "kbcard"
        val executionContext = CollectExecutionContext("", banksaladUserId.toString(), organizationId_kb)

        userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId, "cards", System.currentTimeMillis())
        userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId_kb, "cards", System.currentTimeMillis())

        var userSyncStatus = userSyncStatusRepository.findAll().filter { it.banksaladUserId == banksaladUserId && it.isDeleted == false }
        Assert.assertEquals(2, userSyncStatus.size)

        userSyncStatusService.updateDeleteFlagByUserIdAndCompanyId(executionContext)

        userSyncStatus = userSyncStatusRepository.findAll().filter { it.banksaladUserId == banksaladUserId && it.isDeleted == false }
        Assert.assertEquals(1, userSyncStatus.size)
        Assert.assertEquals(organizationId, userSyncStatus[0].organizationId)
    }

    @Test
    fun updateUserSyncStatus_updateByUserId() {
        val banksaladUserId = 5.toLong()
        val organizationId = "shinhancard"
        val organizationId_kb = "kbcard"

        userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId, "cards", System.currentTimeMillis())
        userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId_kb, "cards", System.currentTimeMillis())

        var userSyncStatus = userSyncStatusRepository.findAll().filter { it.banksaladUserId == banksaladUserId && it.isDeleted == false }
        Assert.assertEquals(2, userSyncStatus.size)

        userSyncStatusService.updateDeleteFlagByUserId(banksaladUserId)
        userSyncStatus = userSyncStatusRepository.findAll().filter { it.banksaladUserId == banksaladUserId && it.isDeleted == false }
        Assert.assertEquals(0, userSyncStatus.size)
    }
}
