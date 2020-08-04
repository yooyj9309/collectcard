package com.rainist.collectcard.cardcreditlimit

import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.db.repository.CreditLimitHistoryRepository
import com.rainist.collectcard.common.db.repository.CreditLimitRepository
import com.rainist.collectcard.common.dto.SyncRequest
import com.rainist.collectcard.common.service.HeaderService
import java.math.BigDecimal
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
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
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("신용한도조회")
class CardCreditLimitServiceTest {
    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardCreditLimitService: CardCreditLimitService

    @Autowired
    lateinit var cardCreditLimitRepository: CreditLimitRepository

    @Autowired
    lateinit var cardCreditLimitHistoryRepository: CreditLimitHistoryRepository

    @MockBean
    lateinit var headerService: HeaderService

    @Test
    fun cardCreditLimitTest() {
        setupServer()

        val syncRequest = SyncRequest(1L, "organizationId")

        given(headerService.makeHeader(syncRequest.banksaladUserId.toString(), syncRequest.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        val creditLimit = cardCreditLimitService.cardCreditLimit(syncRequest)
        Assert.assertEquals(
            creditLimit.dataBody?.creditLimitInfo?.onetimePaymentLimit?.totalLimitAmount,
            BigDecimal(10000000)
        )
        Assert.assertEquals(creditLimit.dataBody?.creditLimitInfo?.cardLoanLimit?.totalLimitAmount, BigDecimal(3000000))

        var listCreditLimitEntity = cardCreditLimitRepository.findAll()
        var listCreditLimitHistoryEntity = cardCreditLimitHistoryRepository.findAll()
        Assert.assertEquals(listCreditLimitEntity.size, 1)
        Assert.assertEquals(listCreditLimitHistoryEntity.size, 1)
        Assert.assertEquals(listCreditLimitEntity[0].onetimePaymentLimitAmount, BigDecimal("10000000.00"))

        val creditLimit2 = cardCreditLimitService.cardCreditLimit(syncRequest)
        Assert.assertEquals(
            creditLimit2.dataBody?.creditLimitInfo?.onetimePaymentLimit?.totalLimitAmount,
            BigDecimal(7100000)
        )
        Assert.assertEquals(creditLimit2.dataBody?.creditLimitInfo?.cardLoanLimit?.totalLimitAmount, BigDecimal(0))

        listCreditLimitEntity = cardCreditLimitRepository.findAll()
        listCreditLimitHistoryEntity = cardCreditLimitHistoryRepository.findAll()
        Assert.assertEquals(listCreditLimitEntity.size, 1)
        Assert.assertEquals(listCreditLimitHistoryEntity.size, 2)
        Assert.assertEquals(listCreditLimitEntity[0].onetimePaymentLimitAmount, BigDecimal("7100000.00"))
    }

    fun setupServer() {
        val creditLimitAPI = ShinhancardApis.card_shinhancard_credit_limit
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(creditLimitAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(creditLimitAPI.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_credit_limit_expected_1.json"))
            )

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(creditLimitAPI.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(creditLimitAPI.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/shinhancard/card_credit_limit_expected_2.json"))
            )
    }

    private fun readText(fileInClassPath: String): String {
        return ResourceUtils.getFile(fileInClassPath).readText(Charsets.UTF_8)
    }
}
