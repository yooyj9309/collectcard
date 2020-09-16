package com.rainist.collectcard.cardtransaction

import com.rainist.collect.common.api.Api
import com.rainist.collectcard.cardtransactions.CardTransactionService
import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponseDataBody
import com.rainist.collectcard.cardtransactions.dto.sortListTransactionsResponseByDesc
import com.rainist.collectcard.cardtransactions.util.CardTransactionUtil
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.db.repository.CardTransactionRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.common.util.DateTimeUtil
import java.math.BigDecimal
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("카드이용내역")
class CardTransactionServiceTest {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardTransactionService: CardTransactionService

    @Autowired
    lateinit var cardTransactionRepository: CardTransactionRepository

    @Autowired
    lateinit var userSyncStatusService: UserSyncStatusService

    @MockBean
    lateinit var headerService: HeaderService

    @Test
    fun cardTransactionTest1() {
        // 최초 내역 조회 (페이징)
        setupServerPaging()
        val executionContext = requestSetting()
        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardTransaction.name,
            DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(),
            true
        )

        val transactions = cardTransactionService.listTransactions(executionContext)

        val entitys = cardTransactionRepository.findAll()

        assertEquals(8, transactions.dataBody?.transactions?.size)
        assertEquals(8, entitys.size)
    }

    @Test
    fun cardTransactionTest2() {
        // 기존 내역 조회 (기존과 동일한 내용을 조회했을때, 변경이 있는지 테스트)
        setupServerPaging()
        val executionContext = requestSetting()
        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardTransaction.name,
            DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(),
            true
        )

        val transactions = cardTransactionService.listTransactions(executionContext)
        val entitys = cardTransactionRepository.findAll()
        // transactions. size비교
        // entitys size비교
        assertEquals(8, transactions.dataBody?.transactions?.size)
        assertEquals(8, entitys.size)
    }

    @Test
    fun cardTransactionTest3() {
        // 추가 내역 조회 (기존 로직 + 추가 로직, 그리고 조회 결과가 전부 신규인경우 테스트)
        setupServer_updated()
        val executionContext = requestSetting()
        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardTransaction.name,
            DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(),
            true
        )

        val transactions = cardTransactionService.listTransactions(executionContext)
        val entitys = cardTransactionRepository.findAll()

        assertEquals(1, transactions.dataBody?.transactions?.size)
        assertEquals(9, entitys.size)

        val cardTransaction = transactions.dataBody?.transactions?.get(0) ?: CardTransaction()

        val sourceEntity = CardTransactionUtil.makeCardTransactionEntity(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            cardTransaction
        )

        val approvalYearMonth = try {
            cardTransaction.approvalDay?.substring(0, 6) ?: ""
        } catch (e: ArrayIndexOutOfBoundsException) {
            ""
        }

        val targetEntity =
            cardTransactionRepository.findByApprovalYearMonthAndBanksaladUserIdAndAndCardCompanyIdAndCardCompanyCardIdAndApprovalNumberAndApprovalDayAndApprovalTime(
                approvalYearMonth,
                executionContext.userId.toLong(),
                executionContext.organizationId,
                cardTransaction.cardCompanyCardId,
                cardTransaction.approvalNumber,
                cardTransaction.approvalDay,
                cardTransaction.approvalTime
            )

        assertNotNull(targetEntity)

        assertEquals(sourceEntity.cardCompanyCardId, targetEntity?.cardCompanyCardId)
        assertEquals(sourceEntity.approvalNumber, targetEntity?.approvalNumber)
        assertEquals(sourceEntity.approvalDay, targetEntity?.approvalDay)
        assertEquals(sourceEntity.approvalTime, targetEntity?.approvalTime)
        assertEquals(sourceEntity.cardName, targetEntity?.cardName)
        assertEquals(sourceEntity.cardNumber, targetEntity?.cardNumber)
        assertEquals(sourceEntity.cardNumberMask, targetEntity?.cardNumberMask)
        assertEquals(sourceEntity.businessLicenseNumber, targetEntity?.businessLicenseNumber)
        assertEquals(sourceEntity.storeName, targetEntity?.storeName)
        assertEquals(sourceEntity.storeNumber, targetEntity?.storeNumber)
        assertEquals(sourceEntity.cardType, targetEntity?.cardType)
        assertEquals(sourceEntity.cardTypeOrigin, targetEntity?.cardTypeOrigin)
        assertEquals(sourceEntity.cardTransactionType, targetEntity?.cardTransactionType)
        assertEquals(sourceEntity.cardTransactionTypeOrigin, targetEntity?.cardTransactionTypeOrigin)
        assertEquals(sourceEntity.currencyCode, targetEntity?.currencyCode)
        assertEquals(sourceEntity.isInstallmentPayment, targetEntity?.isInstallmentPayment)
    }

    @Test
    @DisplayName("카드 취소 내역 테스트")
    @Transactional
    @Rollback
    fun cancelTransactionTest() {
        setupServerCancelAmount()

        val executionContext = requestSetting()
        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardTransaction.name,
            DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(),
            true
        )

        val transactions = cardTransactionService.listTransactions(executionContext)
        val entities = cardTransactionRepository.findAll()

        assertEquals(9, transactions.dataBody?.transactions?.size)
        assertEquals(9, entities.size)

        entities.forEach {
            assertNotNull(it.amount)
        }

        val cancelEntity = cardTransactionRepository
            .findByApprovalYearMonthAndBanksaladUserIdAndAndCardCompanyIdAndCardCompanyCardIdAndApprovalNumberAndApprovalDayAndApprovalTime(
                "202009",
                1,
                "shinhancard",
                "",
                "46835685",
                "20200904",
                "203315"
            )

        assertNotNull(cancelEntity)
        assertNotNull(cancelEntity?.canceledAmount)
        assertEquals(cancelEntity?.canceledAmount?.setScale(4), BigDecimal.valueOf(12800).setScale(4))
    }

    fun setupServerPaging() {
        val creditDomesticAPI = ShinhancardApis.card_shinhancard_credit_domestic_transactions
        val creditOverseaAPI = ShinhancardApis.card_shinhancard_credit_oversea_transactions
        val checkDomesticAPI = ShinhancardApis.card_shinhancard_check_domestic_transactions
        val checkOverseaAPI = ShinhancardApis.card_shinhancard_check_oversea_transactions
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        // 신용카드 국내
        settingOnceMockServiceServer(
            server,
            creditDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_domestic_p1.json"
        )
        settingOnceMockServiceServer(
            server,
            creditDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_domestic_p2.json"
        )
        // 신용카드 해외
        settingOnceMockServiceServer(
            server,
            creditOverseaAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_oversea_p1.json"
        )
        settingOnceMockServiceServer(
            server,
            creditOverseaAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_oversea_p2.json"
        )
        // 체크카드 국내
        settingOnceMockServiceServer(
            server,
            checkDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_domestic_p1.json"
        )
        settingOnceMockServiceServer(
            server,
            checkDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_domestic_p2.json"
        )
        // 체크카드 해외
        settingOnceMockServiceServer(
            server,
            checkOverseaAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_oversea_p1.json"
        )
        settingOnceMockServiceServer(
            server,
            checkOverseaAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_oversea_p2.json"
        )
    }

    private fun setupServer_updated() {
        val creditDomesticAPI = ShinhancardApis.card_shinhancard_credit_domestic_transactions
        val creditOverseaAPI = ShinhancardApis.card_shinhancard_credit_oversea_transactions
        val checkDomesticAPI = ShinhancardApis.card_shinhancard_check_domestic_transactions
        val checkOverseaAPI = ShinhancardApis.card_shinhancard_check_oversea_transactions
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        settingOnceMockServiceServer(
            server,
            creditDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_domestic_updated.json"
        )
        settingOnceMockServiceServer(
            server,
            creditOverseaAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_oversea_updated.json"
        )
        settingOnceMockServiceServer(
            server,
            checkDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_domestic_updated.json"
        )
        settingOnceMockServiceServer(
            server,
            checkOverseaAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_oversea_updated.json"
        )
    }

    private fun setupServerCancelAmount() {
        val creditDomesticAPI = ShinhancardApis.card_shinhancard_credit_domestic_transactions
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        settingOnceMockServiceServer(
            server,
            creditDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_cancelamount_1_credit_domestic_p1.json"
        )
    }

    private fun readText(fileInClassPath: String): String {
        return ResourceUtils.getFile(fileInClassPath).readText(Charsets.UTF_8)
    }

    private fun requestSetting(): CollectExecutionContext {

        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = "shinhancard",
            userId = "1",
            startAt = DateTimeUtil.utcNowLocalDateTime()
        )

        BDDMockito.given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )
        return executionContext
    }

    private fun settingOnceMockServiceServer(server: MockRestServiceServer, api: Api, filePath: String) {
        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(api.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(api.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText(filePath))
            )
    }

    @Test()
    @DisplayName("거래내역 sorting 테스트")
    fun sortingTest() {
        val response = ListTransactionsResponse()

        val transaction1 = CardTransaction().apply {
            this.approvalDay = "20190202"
            this.approvalTime = "130000"
        }

        val transaction2 = CardTransaction().apply {
            this.approvalDay = "20200202"
            this.approvalTime = "130000"
        }

        val transaction3 = CardTransaction().apply {
            this.approvalDay = "20200202"
            this.approvalTime = "170000"
        }

        val transaction4 = CardTransaction().apply {
            this.approvalDay = "20220202"
            this.approvalTime = "130000"
        }

        val mockTransactions = mutableListOf(transaction4, transaction2, transaction3, transaction1)
        response.dataBody = ListTransactionsResponseDataBody().apply {
            this.transactions = mockTransactions
        }

        response.sortListTransactionsResponseByDesc()

        assertEquals(mockTransactions[0], transaction4)
        assertEquals(mockTransactions[1], transaction3)
        assertEquals(mockTransactions[2], transaction2)
        assertEquals(mockTransactions[3], transaction1)
    }
}
