package com.rainist.collectcard.cardbills

import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("카드 청구서 서비스 내부 함수 테스트")
class CardBillServiceImplTest {
    @Autowired
    lateinit var cardBillService: CardBillServiceImpl

    @Test
    fun testPostProgressWithShinhancardOrganization() {
        val organizationId = "shinhancard"

        val bills = mutableListOf<CardBill>()
        val transactions = mutableListOf<CardBillTransaction>()
        transactions.add(CardBillTransaction().apply { cardNumber = "123456******6789" })
        transactions.add(CardBillTransaction().apply { cardNumber = "123" })
        bills.add(CardBill().apply { this.transactions = transactions })

        cardBillService.postProgress(organizationId, bills)
        assertEquals("1234********6789", transactions[0].cardNumber)
        assertEquals("123", transactions[1].cardNumber)
    }

    @Test
    fun testPostProgressWithDefault() {
        val organizationId = "default"
        val bills = mutableListOf<CardBill>()
        val transactions = mutableListOf<CardBillTransaction>()
        transactions.add(CardBillTransaction().apply { cardNumber = "123456******6789" })
        transactions.add(CardBillTransaction().apply { cardNumber = "123" })
        bills.add(CardBill().apply { this.transactions = transactions })

        cardBillService.postProgress(organizationId, bills)
        assertEquals("123456******6789", transactions[0].cardNumber)
        assertEquals("123", transactions[1].cardNumber)
    }
}
