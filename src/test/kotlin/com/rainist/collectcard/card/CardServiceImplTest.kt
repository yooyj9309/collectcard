package com.rainist.collectcard.card

import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.card.dto.ListCardsResponseDataBody
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("보유카드조회 서비스 내부 테스트")
class CardServiceImplTest {
    @Autowired
    lateinit var cardServiceImpl: CardServiceImpl

    @Test
    fun testPostProgressWithShinhancardOrganization() {
        val organizationId = "shinhancard"
        val cards = mutableListOf<Card>()
        cards.add(Card().apply { this.cardNumber = "123456******6789" })
        cards.add(Card().apply { this.cardNumber = "123" })

        val listCardsResponse = ListCardsResponse(
            dataBody = ListCardsResponseDataBody(cards = cards, nextKey = null),
            dataHeader = null
        )
        cardServiceImpl.postProgress(organizationId, listCardsResponse)
        assertEquals("1234********6789", cards[0].cardNumber)
        assertEquals("123", cards[1].cardNumber)
    }

    @Test
    fun testPostProgressWithDefault() {
        val anotherOrganizationId = "anotherShinhancard"
        val cards = mutableListOf<Card>()
        cards.add(Card().apply { this.cardNumber = "123456******6789" })
        cards.add(Card().apply { this.cardNumber = "123" })
        val listCardsResponse = ListCardsResponse(
            dataBody = ListCardsResponseDataBody(cards = cards, nextKey = null),
            dataHeader = null
        )
        cardServiceImpl.postProgress(anotherOrganizationId, listCardsResponse)
        assertEquals("123456******6789", cards[0].cardNumber)
        assertEquals("123", cards[1].cardNumber)
    }
}
