package com.rainist.collectcard.card

import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.crypto.HashUtil
import com.rainist.collectcard.common.db.entity.CardEntity
import com.rainist.collectcard.common.db.repository.CardRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.enums.CardOwnerType
import com.rainist.collectcard.common.enums.CardType
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import java.util.UUID
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
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
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("보유카드조회")
class CardServiceImplTest {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardService: CardService

    @Autowired
    lateinit var cardRepository: CardRepository

    @MockBean
    lateinit var headerService: HeaderService

    @Test
    @Rollback
    @Transactional
    fun listCard_success() {
        setupServer(listOf("mock/shinhancard/card/card_shinhancard_cards.json"))

        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = "shinhancard",
            userId = DateTimeUtil.kstLocalDateTimeToEpochMilliSecond().toString(),
            startAt = DateTimeUtil.utcNowLocalDateTime()
        )

        given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        val response = cardService.listCards(executionContext)

        assertThat(response.dataHeader?.resultCode, `is`(ResultCode.OK))
        assertThat(response.dataBody?.cards?.size, `is`(4))
        assertThat(
            response.dataBody?.cards?.first(), `is`(Card().apply {
                cardCompanyId = "shinhancard"
                cardCompanyCardId = HashUtil.sha256("9523*********8721")
                cardCompanyCardIdOrigin = "9523*********8721"
                cardOwnerName = "홍길동"
                cardOwnerType = CardOwnerType.SELF
                cardOwnerTypeOrigin = "1"
                cardName = "Deep Store[딥 스토어]"
                internationalBrandName = "CARD_INTERNATIONAL_BRAND_MASTERCARD"
                cardNumber = "9523*********8721"
                cardNumberMask = "9523*********8721"
                cardType = CardType.CREDIT
                cardTypeOrigin = "1"
                isBusinessCard = false
                trafficSupported = true
            }
            ))

        val cardEntities = cardRepository.findAll()
        assertThat(cardEntities.size, `is`(4))

        val firstEntity = cardEntities.first()

        val now = LocalDateTime.of(2001, 4, 20, 12, 30)
        cardEntities.first().apply {
            lastCheckAt = now
            createdAt = now
            updatedAt = now
        }
        assertThat(
            firstEntity, `is`(
                CardEntity(
                    cardId = firstEntity.cardId,
                    banksaladUserId = executionContext.userId.toLong(),
                    cardCompanyId = "shinhancard",
                    cardCompanyCardId = HashUtil.sha256("9523*********8721"),
                    cardCompanyCardIdOrigin = "9523*********8721",
                    lastCheckAt = now,
                    cardOwnerName = "홍길동",
                    cardOwnerType = "SELF",
                    cardOwnerTypeOrigin = "1",
                    cardName = "Deep Store[딥 스토어]",
                    internationalBrandName = "CARD_INTERNATIONAL_BRAND_MASTERCARD",
                    cardNumber = "9523*********8721",
                    cardNumberMask = "9523*********8721",
                    cardType = CardType.CREDIT.name,
                    cardTypeOrigin = "1",
                    isBusinessCard = false,
                    createdAt = now,
                    updatedAt = now
                )
            )
        )
    }

    @Test
    @Rollback
    @Transactional
    fun listCard_pagination() {
        setupServer(
            listOf(
                "mock/shinhancard/card/card_shinhancard_cards_paging_1.json",
                "mock/shinhancard/card/card_shinhancard_cards_paging_2.json"
            )
        )

        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = "shinhancard",
            userId = DateTimeUtil.kstLocalDateTimeToEpochMilliSecond().toString(),
            startAt = DateTimeUtil.utcNowLocalDateTime()
        )

        given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        val response = cardService.listCards(executionContext)

        assertThat(response.dataHeader?.resultCode, `is`(ResultCode.OK))
        assertThat(response.dataBody?.cards?.size, `is`(19))

        assertThat(
            response.dataBody?.cards?.first(), `is`(Card().apply {
                cardCompanyId = "shinhancard"
                cardCompanyCardId = HashUtil.sha256("9523*********8721")
                cardCompanyCardIdOrigin = "9523*********8721"
                cardOwnerName = "홍길동"
                cardOwnerType = CardOwnerType.SELF
                cardOwnerTypeOrigin = "1"
                cardName = "Deep Store[딥 스토어]"
                internationalBrandName = "CARD_INTERNATIONAL_BRAND_MASTERCARD"
                cardNumber = "9523*********8721"
                cardNumberMask = "9523*********8721"
                cardType = CardType.CREDIT
                cardTypeOrigin = "1"
                isBusinessCard = false
                trafficSupported = true
            }
            ))

        val cardEntities = cardRepository.findAll()
        assertThat(cardEntities.size, `is`(19))

        val firstEntity = cardEntities.first()

        val now = LocalDateTime.of(2001, 4, 20, 12, 30)
        cardEntities.first().apply {
            lastCheckAt = now
            createdAt = now
            updatedAt = now
        }

        assertThat(
            firstEntity, `is`(
                CardEntity(
                    cardId = firstEntity.cardId,
                    banksaladUserId = executionContext.userId.toLong(),
                    cardCompanyId = "shinhancard",
                    cardCompanyCardId = HashUtil.sha256("9523*********8721"),
                    cardCompanyCardIdOrigin = "9523*********8721",
                    lastCheckAt = now,
                    cardOwnerName = "홍길동",
                    cardOwnerType = "SELF",
                    cardOwnerTypeOrigin = "1",
                    cardName = "Deep Store[딥 스토어]",
                    internationalBrandName = "CARD_INTERNATIONAL_BRAND_MASTERCARD",
                    cardNumber = "9523*********8721",
                    cardNumberMask = "9523*********8721",
                    cardType = "CREDIT",
                    cardTypeOrigin = "1",
                    isBusinessCard = false,
                    createdAt = now,
                    updatedAt = now
                )
            )
        )
    }

    @Test
    @Rollback
    @Transactional
    fun listCard_updated() {
        setupServer(listOf("mock/shinhancard/card/card_shinhancard_cards_update.json"))

        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = "shinhancard",
            userId = DateTimeUtil.kstLocalDateTimeToEpochMilliSecond().toString(),
            startAt = DateTimeUtil.utcNowLocalDateTime()
        )

        given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        val now = LocalDateTime.of(2001, 4, 20, 12, 30)
        cardRepository.save(
            CardEntity(
                cardId = 1,
                banksaladUserId = executionContext.userId.toLong(),
                cardCompanyId = "shinhancard",
                cardCompanyCardId = HashUtil.sha256("9523*********8721"),
                cardCompanyCardIdOrigin = "9523*********8721",
                lastCheckAt = now,
                cardOwnerName = "홍길동",
                cardOwnerType = "SELF",
                cardOwnerTypeOrigin = "1",
                cardName = "Deep Store[딥 스토어]",
                internationalBrandName = "CARD_INTERNATIONAL_BRAND_MASTERCARD",
                cardNumber = "9523*********8721",
                cardNumberMask = "9523*********8721",
                cardType = "CREDIT",
                cardTypeOrigin = "1",
                isBusinessCard = false,
                createdAt = now,
                updatedAt = now
            )
        )

        val response = cardService.listCards(executionContext)

        assertThat(response.dataHeader?.resultCode, `is`(ResultCode.OK))
        assertThat(response.dataBody?.cards?.size, `is`(1))

        assertThat(
            response.dataBody?.cards?.first(), `is`(Card().apply {
                cardCompanyId = "shinhancard"
                cardCompanyCardId = HashUtil.sha256("9523*********8721")
                cardCompanyCardIdOrigin = "9523*********8721"
                cardOwnerName = "홍길동"
                cardOwnerType = CardOwnerType.SELF
                cardOwnerTypeOrigin = "5"
                cardName = "Deep Store[딥 스토어] test"
                internationalBrandName = "CARD_INTERNATIONAL_BRAND_VISA"
                cardNumber = "9523*********8721"
                cardNumberMask = "9523*********8721"
                cardType = CardType.DEBIT
                cardTypeOrigin = "5"
                isBusinessCard = false
                trafficSupported = true
            }
            ))

        val cardEntities = cardRepository.findAll()
        assertThat(cardEntities.size, `is`(1))

        val firstEntity = cardEntities.first()

        cardEntities.first().apply {
            lastCheckAt = now
            createdAt = now
            updatedAt = now
        }
        assertThat(
            firstEntity, `is`(
                CardEntity(
                    cardId = firstEntity.cardId,
                    banksaladUserId = executionContext.userId.toLong(),
                    cardCompanyId = "shinhancard",
                    cardCompanyCardId = HashUtil.sha256("9523*********8721"),
                    cardCompanyCardIdOrigin = "9523*********8721",
                    lastCheckAt = now,
                    cardOwnerName = "홍길동",
                    cardOwnerType = "SELF",
                    cardOwnerTypeOrigin = "5",
                    cardName = "Deep Store[딥 스토어] test",
                    internationalBrandName = "CARD_INTERNATIONAL_BRAND_VISA",
                    cardNumber = "9523*********8721",
                    cardNumberMask = "9523*********8721",
                    cardType = "DEBIT",
                    cardTypeOrigin = "5",
                    isBusinessCard = false,
                    createdAt = now,
                    updatedAt = now
                )
            )
        )
    }

    @Test
    @Rollback
    @Transactional
    fun shinhancardMaskedCardNumberUpdate() {
        setupServer(listOf("mock/shinhancard/card/card_shinhancard_masked_8th.json", "mock/shinhancard/card/card_shinhancard_masked_6th.json"))
        val banksaladUserId = "1"

        val executionContext: CollectExecutionContext = requestSetting(banksaladUserId)

        var response = cardService.listCards(executionContext)
        var cardEntities = cardRepository.findAll()
        // db count 1.
        // db에 들어간 값이 마스킹 번호 일치 여부
        assertEquals(2, cardEntities.size)
        assertEquals("6152*********2234", cardEntities[0].cardNumberMask)

        // db count 1.
        // 업데이트 진행여부확인
        // db cardNumber masked 6th
        response = cardService.listCards(executionContext)
        cardEntities = cardRepository.findAll()
        assertEquals(2, cardEntities.size)
        assertEquals("615266*******2234", cardEntities[0].cardNumberMask)
    }

    private fun requestSetting(banksaladUserId: String): CollectExecutionContext {
        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = "shinhancard",
            userId = banksaladUserId,
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

    private fun setupServer(mockResponseFileNames: List<String>) {
        val api = ShinhancardApis.card_shinhancard_cards
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        mockResponseFileNames.forEach { fileName ->
            server.expect(ExpectedCount.once(), requestTo(api.endpoint))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(api.method.name)))
                .andRespond(
                    MockRestResponseCreators.withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(readText("classpath:$fileName"))
                )
        }
    }

    private fun readText(fileInClassPath: String): String {
        return ResourceUtils.getFile(fileInClassPath).readText(Charsets.UTF_8)
    }
}
