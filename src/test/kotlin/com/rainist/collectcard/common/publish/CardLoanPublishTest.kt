package com.rainist.collectcard.common.publish

import com.rainist.collectcard.cardloans.CardLoanService
import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.publish.banksalad.CardLoanPublishService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.collectcard.common.util.ReflectionCompareUtil
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("카드 Loan publish 테스트")
class CardLoanPublishTest {

    companion object : Log

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardLoanService: CardLoanService

    @Autowired
    lateinit var cardLoanPublishService: CardLoanPublishService

    @MockBean
    lateinit var headerService: HeaderService

    val banksaladUserId = "1"
    val organizationId = "shinhancard"

    @Test
    fun loanShadowingTest() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        val now = DateTimeUtil.utcNowLocalDateTime()
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_loan_info,
            "classpath:mock/shinhancard/loan/card_loan_expected_1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_loan_detail,
            "classpath:mock/shinhancard/loan/card_loan_expected_1_detail_1.json",
            "classpath:mock/shinhancard/loan/card_loan_expected_1_detail_2.json"
        )

        val executionContext: CollectExecutionContext =
            ExecutionTestUtil.getExecutionContext(banksaladUserId, organizationId) as CollectExecutionContext
        BDDMockito.given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        val response = cardLoanService.listCardLoans(executionContext, now)

        val shadowingResponse = cardLoanPublishService.shadowing(
            banksaladUserId.toLong(),
            organizationId,
            now,
            executionContext.executionRequestId,
            response
        )
        Assert.assertEquals(false, shadowingResponse.isDiff)

        val oldLoans = shadowingResponse.oldList as List<Loan>
        val loans = shadowingResponse.dbList as List<Loan>

        val diffFieldMap = ReflectionCompareUtil.reflectionCompareCardLoans(oldLoans, loans)
        assertThat(diffFieldMap.size).isEqualTo(0)
    }
}
