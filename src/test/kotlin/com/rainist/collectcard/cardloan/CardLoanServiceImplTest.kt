package com.rainist.collectcard.cardloan

import com.rainist.collectcard.cardloans.CardLoanService
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.dto.SyncRequest
import com.rainist.collectcard.common.service.HeaderService
import java.math.BigDecimal
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

    @MockBean
    lateinit var headerService: HeaderService

    @Test
    fun cardLoanTest() {
        setupServer()

        val syncRequest = SyncRequest("1", "organizationId")

        BDDMockito.given(headerService.makeHeader(syncRequest.banksaladUserId, syncRequest.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        val loans = cardLoanService.listCardLoans(syncRequest)

        assertEquals(loans.dataBody?.loans?.size, 2)
        assertEquals(loans.dataBody?.loans?.get(0)?.loanName, "스피드론플러스")
        assertEquals(loans.dataBody?.loans?.get(0)?.loanAmount, BigDecimal(3000000))

        assertEquals(loans.dataBody?.loans?.get(1)?.loanName, "스피드론이지")
        assertEquals(loans.dataBody?.loans?.get(1)?.loanAmount, BigDecimal(5000000))
    }

    fun setupServer() {
        val loanInfoAPI = ShinhancardApis.card_shinhancard_loan_info
        val loanDetailAPI = ShinhancardApis.card_shinhancard_loan_detail
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        server.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(loanInfoAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanInfoAPI.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_p1.json"))
            )

        server.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(loanDetailAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanDetailAPI.method.name)))
            .andExpect(content().string("{\"dataBody\":{\"loanNo\":\"0005\",\"cardLoanGbn\":\"1\"},\"dataHeader\":{\"EMPTY\":\"\"}}"))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_p1_detail_p1.json"))
            )

        server.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(loanDetailAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(loanDetailAPI.method.name)))
            .andExpect(content().string("{\"dataBody\":{\"loanNo\":\"0004\",\"cardLoanGbn\":\"1\"},\"dataHeader\":{\"EMPTY\":\"\"}}"))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_loan_expected_p1_detail_p2.json"))
            )
    }

    private fun readText(fileInClassPath: String): String {
        return ResourceUtils.getFile(fileInClassPath).readText(Charsets.UTF_8)
    }
}
