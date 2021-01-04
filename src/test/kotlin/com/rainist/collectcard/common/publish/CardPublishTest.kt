package com.rainist.collectcard.common.publish

import com.rainist.collectcard.card.CardService
import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.publish.banksalad.CardPublishService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.common.util.DateTimeUtil
import java.util.UUID
import org.junit.Assert.assertEquals
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
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("카드 publish 테스트")
class CardPublishTest {

    @Autowired
    lateinit var cardPublishService: CardPublishService

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardService: CardService

    @MockBean
    lateinit var headerService: HeaderService

    @Test
    @Rollback
    @Transactional
    fun cardShadowingTest() {

        val userId = 12345L
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        val now = DateTimeUtil.utcNowLocalDateTime()
        val organizationId = "shinhancard"
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_cards,
            "classpath:mock/shinhancard/card/card_shinhancard_cards.json"
        )

        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = organizationId,
            userId = userId.toString()
        )

        BDDMockito.given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        val response = cardService.listCards(executionContext, now)

        // 같은경우.

        val shadowingResponse = cardPublishService.shadowing(userId, organizationId, now, executionContext.executionRequestId, response)
        assertEquals(false, shadowingResponse.isDiff)

        val listSize = shadowingResponse.oldList.size
        val oldCards = shadowingResponse.oldList as List<Card>
        val cards = shadowingResponse.dbList as List<Card>

        for (i in 0 until listSize) {
            val isDiffObject = listOf(
                oldCards[i].cardCompanyId == cards[i].cardCompanyId,
                oldCards[i].cardCompanyCardId == cards[i].cardCompanyCardId,
                oldCards[i].cardOwnerName == cards[i].cardOwnerName,
                oldCards[i].cardOwnerType == cards[i].cardOwnerType,
                oldCards[i].cardOwnerTypeOrigin == cards[i].cardOwnerTypeOrigin,
                oldCards[i].cardName == cards[i].cardName,
                oldCards[i].cardBrandName == cards[i].cardBrandName,
                oldCards[i].internationalBrandName == cards[i].internationalBrandName,
                oldCards[i].cardNumber == cards[i].cardNumber,
                oldCards[i].cardNumberMask == cards[i].cardNumberMask,
                oldCards[i].cardType == cards[i].cardType,
                oldCards[i].cardTypeOrigin == cards[i].cardTypeOrigin,
                oldCards[i].issuedDay == cards[i].issuedDay,
                oldCards[i].expiresDay == cards[i].expiresDay,
                oldCards[i].cardStatus == cards[i].cardStatus,
                oldCards[i].cardStatusOrigin == cards[i].cardStatusOrigin,
                oldCards[i].lastUseDay == cards[i].lastUseDay,
                oldCards[i].lastUseTime == cards[i].lastUseTime,
                oldCards[i].annualFee == cards[i].annualFee,
                oldCards[i].paymentBankId == cards[i].paymentBankId,
                oldCards[i].paymentAccountNumber == cards[i].paymentAccountNumber,
                oldCards[i].isBusinessCard == cards[i].isBusinessCard
            ).all { it }
            assertEquals(true, isDiffObject)
        }
    }
}
