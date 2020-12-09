package com.rainist.collectcard.cardbills

import com.rainist.collectcard.cardbills.dto.ListCardBillsResponseDataBody
import com.rainist.collectcard.cardbills.dto.toListCardBillsResponseProto
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.db.repository.CardBillHistoryRepository
import com.rainist.collectcard.common.db.repository.CardBillRepository
import com.rainist.collectcard.common.db.repository.CardBillTransactionRepository
import com.rainist.collectcard.common.db.repository.CardPaymentScheduledRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("청구서 서비스 테스트")
class CardBillServiceTest {
    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardBillService: CardBillService

    @MockBean
    lateinit var headerService: HeaderService

    @Autowired
    lateinit var userSyncStatusService: UserSyncStatusService

    @Autowired
    lateinit var cardBillRepository: CardBillRepository

    @Autowired
    lateinit var cardBillHistoryRepository: CardBillHistoryRepository

    @Autowired
    lateinit var cardBillTransactionRepository: CardBillTransactionRepository

    @Autowired
    lateinit var cardPaymentScheduledRepository: CardPaymentScheduledRepository

    /**
     * 청구서는 페이징이 없는 경우,
     * 거래내역은 페이징이 있도록 설정한 후 테스트
     * TODO 295 머지후, MOCK filePath 업데이트 필요
     */
    @Test
    @Transactional
    @Rollback
    fun listUserCardBills_success() {
        val executionContext: CollectExecutionContext = ExecutionTestUtil.getExecutionContext("1", "shinhancard") as CollectExecutionContext
        BDDMockito.given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )
        /**
         *  데이터가 적재되어있지 않은 상태에서 최초조회.
         */

        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardbills.name,
            DateTimeUtil.kstLocalDateTimeToEpochMilliSecond(LocalDateTime.of(2020, 7, 30, 0, 0)),
            true
        )

        setupServer()
        var res = cardBillService.listUserCardBills(executionContext)
        var bills = res?.dataBody ?: ListCardBillsResponseDataBody()

        assertEquals(3, bills.cardBills?.size) // 청구서(2) + 결제예정금액(1)
        assertEquals(12, bills.cardBills?.get(0)?.transactions?.size) // 거래내역
        assertEquals(5, bills.cardBills?.get(1)?.transactions?.size) // 청구서 check or credit
        assertEquals(5, bills.cardBills?.get(2)?.transactions?.size) // 청구서 check or credit

        var billEntities = cardBillRepository.findAll()
        var billHistoryEntities = cardBillHistoryRepository.findAll()
        var billTransactionEntites = cardBillTransactionRepository.findAll()
        var cardPaymentScheduledEntities = cardPaymentScheduledRepository.findAll()

        assertEquals(2, billEntities.size) // 청구서 : 2개
        assertEquals(2, billHistoryEntities.size) // 청구서 : 2개
        assertEquals(10, billTransactionEntites.size) // transaction : 10
        assertEquals(12, cardPaymentScheduledEntities.size) // 결제예정금액 : 12개

        val protoRes = res.toListCardBillsResponseProto()
        assertEquals("2020-09-14", protoRes.getData(0).dueDate)
        assertEquals(281731.toDouble(), protoRes.getData(0).totalAmount)
        assertEquals(12, protoRes.getData(0).transactionsList.size)

        /**
         * entity가 적재된 상태에서 재조회
         */

        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardbills.name,
            DateTimeUtil.kstLocalDateTimeToEpochMilliSecond(LocalDateTime.of(2020, 7, 30, 0, 0)),
            true
        )

        setupServer_withUpdate()
        res = cardBillService.listUserCardBills(executionContext)
        bills = res?.dataBody ?: ListCardBillsResponseDataBody()

        billEntities = cardBillRepository.findAll()
        billHistoryEntities = cardBillHistoryRepository.findAll()
        billTransactionEntites = cardBillTransactionRepository.findAll()
        cardPaymentScheduledEntities = cardPaymentScheduledRepository.findAll()

        assertEquals(2, billEntities.size) // 청구서 : 2개
        assertEquals(3, billHistoryEntities.size) // 청구서 히스토리 : 3개  // 신용 bill 업데이트

        // TODO 추후에 db 인덱스 적용 및 서비스 코드 주석해제시 해당부분 적
        // assertEquals(15, billTransactionEntites.size) // transaction : 15 // 삭제된 내역 5개
        // assertEquals(10, billTransactionEntites.filter { it.isDeleted == false }.size) // 현재 오픈된 내용 10개
        assertEquals(12, cardPaymentScheduledEntities.size) // 결제예정금액 : 12개
    }

    private fun setupServer() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_check_bills, "classpath:mock/shinhancard/bill/bill_check_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_credit_bills, "classpath:mock/shinhancard/bill/bill_credit_expected_1.json")

        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_check_bill_transactions, "classpath:mock/shinhancard/bill/bill_check_detail_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_credit_bill_transactions, "classpath:mock/shinhancard/bill/bill_credit_detail_expected_1.json")

        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected, "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p2.json")

        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected, "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p2.json")
    }

    private fun setupServer_withUpdate() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_check_bills, "classpath:mock/shinhancard/bill/bill_check_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_credit_bills, "classpath:mock/shinhancard/bill/bill_credit_expected_updated_1.json")

        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_check_bill_transactions, "classpath:mock/shinhancard/bill/bill_check_detail_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_credit_bill_transactions, "classpath:mock/shinhancard/bill/bill_credit_detail_expected_1.json")

        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected, "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p2.json")

        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected, "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p2.json")
    }

    private fun readText(fileInClassPath: String): String {
        return ResourceUtils.getFile(fileInClassPath).readText(Charsets.UTF_8)
    }
}
