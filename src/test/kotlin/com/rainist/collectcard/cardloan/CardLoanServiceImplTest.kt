package com.rainist.collectcard.cardloan

import com.rainist.collectcard.cardloans.CardLoanService
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.db.repository.CardLoanHistoryRepository
import com.rainist.collectcard.common.db.repository.CardLoanRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.common.util.DateTimeUtil
import java.math.BigDecimal
import java.util.UUID
import junit.framework.Assert.assertEquals
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
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.match.MockRestRequestMatchers.content
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("카드대출리스트")
class CardLoanServiceImplTest {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardLoanService: CardLoanService

    @Autowired
    lateinit var cardLoanRepository: CardLoanRepository

    @Autowired
    lateinit var cardLoanHistoryRepository: CardLoanHistoryRepository

    @MockBean
    lateinit var headerService: HeaderService

    /**
     * 최초 두 개의 항목조회
     * cardloan 2개, history 2개
     */
    @Test
    fun cardLoanTest() {
        setupServer()

        val executionContext = requestSetting()

        val loans = cardLoanService.listCardLoans(executionContext)
        val listLoan = cardLoanRepository.findAll()
        val listLoanHistory = cardLoanHistoryRepository.findAll()
        assertEquals(loans.dataBody?.loans?.size, 2)
        assertEquals(loans.dataBody?.loans?.get(0)?.loanName, "스피드론플러스")
        assertEquals(loans.dataBody?.loans?.get(0)?.loanAmount, BigDecimal(3000000))

        assertEquals(loans.dataBody?.loans?.get(1)?.loanName, "스피드론이지")
        assertEquals(loans.dataBody?.loans?.get(1)?.loanAmount, BigDecimal(5000000))

        assertEquals(listLoan.size, 2)
        assertEquals(listLoanHistory.size, 2)
    }

    /**
     * 이전에 조회했던 항목 조회 (변동 x)
     * cardloan 2개, history 2개
     */
    @Test
    fun cardLoanTest2() {
        setupServer()

        val syncRequest = requestSetting()

        val loans = cardLoanService.listCardLoans(syncRequest)
        val listLoan = cardLoanRepository.findAll()
        val listLoanHistory = cardLoanHistoryRepository.findAll()

        // 단순 재조회 , history가 쌓이면 안됀다.
        assertEquals(listLoan.size, 2)
        assertEquals(listLoanHistory.size, 2)
    }

    /**
     * paging된 데이터 조회
     * 기존 대출내역 1개 변경(대출 업데이트 및 히스토리 + 1) 및 대출 리스트 한 개 추가. ( 대출 리스트 및 히스토리 +1 )
     * cardloan 3개, history 4개
     */
    @Test
    fun cardLoanTest3() {
        setupServerPaging()

        val syncRequest = requestSetting()

        val loans = cardLoanService.listCardLoans(syncRequest)
        val listLoan = cardLoanRepository.findAll()
        val listLoanHistory = cardLoanHistoryRepository.findAll()

        // 대출 한개 추가, 한개 변경. == history 2개 추가. 대출 1개 추가.
        assertEquals(listLoan.size, 3)
        assertEquals(listLoanHistory.size, 4)
        assertEquals(loans.dataBody?.loans?.get(1)?.remainingAmount, BigDecimal("4000000"))
    }

    fun setupServer() {
        val loanInfoAPI = ShinhancardApis.card_shinhancard_loan_info
        val loanDetailAPI = ShinhancardApis.card_shinhancard_loan_detail
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(loanInfoAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanInfoAPI.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_1.json"))
            )

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(loanDetailAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanDetailAPI.method.name)))
            .andExpect(content().string("{\"dataBody\":{\"loanNo\":\"0005\",\"cardLoanGbn\":\"1\"},\"dataHeader\":{\"EMPTY\":\"\"}}"))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_1_detail_1.json"))
            )

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(loanDetailAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanDetailAPI.method.name)))
            .andExpect(content().string("{\"dataBody\":{\"loanNo\":\"0004\",\"cardLoanGbn\":\"1\"},\"dataHeader\":{\"EMPTY\":\"\"}}"))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_1_detail_2.json"))
            )
    }

    fun setupServerPaging() {
        val loanInfoAPI = ShinhancardApis.card_shinhancard_loan_info
        val loanDetailAPI = ShinhancardApis.card_shinhancard_loan_detail
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(loanInfoAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanInfoAPI.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_2_p1.json"))
            )

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(loanInfoAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanInfoAPI.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_2_p2.json"))
            )

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(loanDetailAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanDetailAPI.method.name)))
            .andExpect(content().string("{\"dataBody\":{\"loanNo\":\"0005\",\"cardLoanGbn\":\"1\"},\"dataHeader\":{\"EMPTY\":\"\"}}"))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_2_detail_1.json"))
            )

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(loanDetailAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanDetailAPI.method.name)))
            .andExpect(content().string("{\"dataBody\":{\"loanNo\":\"0004\",\"cardLoanGbn\":\"1\"},\"dataHeader\":{\"EMPTY\":\"\"}}"))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_2_detail_2.json"))
            )

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(loanDetailAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanDetailAPI.method.name)))
            .andExpect(content().string("{\"dataBody\":{\"loanNo\":\"0006\",\"cardLoanGbn\":\"1\"},\"dataHeader\":{\"EMPTY\":\"\"}}"))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_2_detail_3.json"))
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
}
