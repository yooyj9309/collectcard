package com.rainist.collectcard.common.publish

import com.rainist.collectcard.cardbills.CardBillService
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.publish.banksalad.CardBillPublishService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.collectcard.common.util.ReflectionCompareUtil
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import javax.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest
@DisplayName("카드 청구서 publish 테스트")
class CardBillPublishTest {

    @Autowired
    lateinit var cardBillPublishService: CardBillPublishService

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardBillService: CardBillService

    @Autowired
    lateinit var userSyncStatusService: UserSyncStatusService

    @MockBean
    lateinit var headerService: HeaderService

    val userId = 1L
    val organizationId = "shinhancard"

    companion object : Log

    @Test
    @Rollback
    @Transactional
    fun cardBillShadowingTest() {
        val now = DateTimeUtil.utcNowLocalDateTime()
        val executionContext = ExecutionTestUtil.getExecutionContext("1", "shinhancard", now) as CollectExecutionContext
        given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardbills.name,
            DateTimeUtil.kstLocalDateTimeToEpochMilliSecond(LocalDateTime.of(2020, 7, 30, 0, 0)),
            true
        )

        setupServerPaging()
        val response = cardBillService.listUserCardBills(executionContext, now)

        val shadowingResponse = cardBillPublishService.shadowing(
            userId,
            organizationId,
            now,
            executionContext.executionRequestId,
            response
        )

        val oldBills = shadowingResponse.oldList as List<CardBill>
        val shadowingBills = shadowingResponse.dbList as List<CardBill>

        val diffFieldMap = ReflectionCompareUtil.reflectionCompareBills(oldBills, shadowingBills)
        assertThat(diffFieldMap.size).isEqualTo(0)
    }

    private fun setupServerPaging() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_check_bills,
            "classpath:mock/shinhancard/bill/bill_check_expected_1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_credit_bills,
            "classpath:mock/shinhancard/bill/bill_credit_expected_1.json"
        )

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_check_bill_transactions,
            "classpath:mock/shinhancard/bill/bill_check_detail_expected_1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_credit_bill_transactions,
            "classpath:mock/shinhancard/bill/bill_credit_detail_expected_1.json"
        )

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p2.json"
        )

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p2.json"
        )
    }
}
