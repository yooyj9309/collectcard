package com.rainist.collectcard.card

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.card.dto.ListCardsRequest
import com.rainist.collectcard.card.dto.ListCardsRequestDataBody
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.enums.CardOwnerType
import com.rainist.collectcard.common.enums.CardType
import com.rainist.collectcard.common.execution.MockExecutions
import com.rainist.collectcard.common.util.ExecutionTestUtil
import junit.framework.Assert.assertEquals
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("카Execution 테스트")
class CardExecutionTest {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var collectExecutorService: CollectExecutorService

    @Test
    fun cardExecutionTest() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_cards,
            "classpath:mock/shinhancard/card/card_shinhancard_cards.json"
        )

        val res: ExecutionResponse<ListCardsResponse> = ExecutionTestUtil.getExecutionResponse(
            collectExecutorService,
            MockExecutions.shinhancardCards,
            ExecutionTestUtil.getExecutionContext("1", "shinhancard"),
            makeCardRequest()
        )

        val cards = res.response?.dataBody?.cards ?: mutableListOf()

        assertEquals(cards.size, 4)
        assertThat(cards[0]).isEqualToComparingFieldByField(Card().apply {
            this.cardCompanyCardId = "9523*********8721"
            this.cardOwnerName = "홍길동"
            this.cardOwnerType = CardOwnerType.SELF
            this.cardOwnerTypeOrigin = "1"
            this.cardName = "Deep Store[딥 스토어]"
            this.internationalBrandName = "CARD_INTERNATIONAL_BRAND_MASTERCARD"
            this.cardNumber = "9523-****-*****-8721"
            this.cardNumberMask = "9523-****-*****-8721"
            this.cardType = CardType.CREDIT
            this.cardTypeOrigin = "1"
            this.isBusinessCard = false
            this.isTrafficSupported = true
        })

        assertThat(cards[1]).isEqualToComparingFieldByField(Card().apply {
            this.cardCompanyCardId = "5774*********1742"
            this.cardOwnerName = "홍길동"
            this.cardOwnerType = CardOwnerType.SELF
            this.cardOwnerTypeOrigin = "5"
            this.cardName = "하이패스(체크)신한은행"
            this.internationalBrandName = "CARD_INTERNATIONAL_BRAND_NOT_EXISTS"
            this.cardNumber = "5774-****-*****-1742"
            this.cardNumberMask = "5774-****-*****-1742"
            this.cardType = CardType.DEBIT
            this.cardTypeOrigin = "5"
            this.isBusinessCard = false
            this.isTrafficSupported = false
        })
    }

    fun makeCardRequest(): ExecutionRequest<ListCardsRequest> {
        return ExecutionRequest.builder<ListCardsRequest>()
            .headers(mutableMapOf<String, String?>())
            .request(ListCardsRequest().apply {
                dataBody = ListCardsRequestDataBody()
            })
            .build()
    }
}
