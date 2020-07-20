package com.rainist.collectcard.common.service

import com.rainist.collectcard.common.db.repository.UserSyncStatusRepository
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

        var userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionId(
            banksaladUserId,
            organizationId,
            transactionId
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

        var userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionId(
            banksaladUserId,
            organizationId,
            transactionId
        )

        Assert.assertEquals(lastCheckAt, userSyncStatusEntity?.lastCheckAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())

        val lastCheckAt2 = System.currentTimeMillis() + 1000
        userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt2)

        userSyncStatusEntity = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndTransactionId(
            banksaladUserId,
            organizationId,
            transactionId
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
        Assert.assertEquals(0, lastCheckAtFromDB)

        var lastCheckAt = System.currentTimeMillis()
        userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId, transactionId, lastCheckAt)

        lastCheckAtFromDB =
            userSyncStatusService.getUserSyncStatusLastCheckAt(banksaladUserId, organizationId, transactionId)
        Assert.assertEquals(lastCheckAt, lastCheckAtFromDB)
    }
}
