package com.rainist.collectcard.card

import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.card.mapper.CardMapper
import com.rainist.collectcard.common.db.entity.CardEntity
import com.rainist.collectcard.common.enums.CardOwnerType
import com.rainist.collectcard.common.enums.CardStatus
import com.rainist.collectcard.common.enums.CardType
import java.math.BigDecimal
import java.time.LocalDateTime
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers

@DisplayName("CardMapper 테스트")
class CardMapperTest {

    val cardMapper = Mappers.getMapper(CardMapper::class.java)

    @Test
    @DisplayName("CardEntity -> CardDto 변환 테스트")
    fun entityToDtoTest() {
        val now = LocalDateTime.now()
        val entity = createCardEntity(now)
        val dto = cardMapper.toCardDto(entity)

        Assert.assertEquals(dto.cardId, null)
        Assert.assertEquals(dto.cardCompanyCardId, "SHC")
        Assert.assertEquals(dto.cardOwnerName, "홍길동")
        Assert.assertEquals(dto.cardOwnerType, CardOwnerType.FAMILY)
        Assert.assertEquals(dto.cardOwnerTypeOrigin, "본인")
        Assert.assertEquals(dto.cardName, "나라사랑카드")
        Assert.assertEquals(dto.cardBrandName, "BC")
        Assert.assertEquals(dto.internationalBrandName, "SHINHANCARD")
        Assert.assertEquals(dto.cardNumber, "1234-5678-1234-5678")
        Assert.assertEquals(dto.cardNumberMask, "****-5678-1234-****")
        Assert.assertEquals(dto.cardType, CardType.CREDIT)
        Assert.assertEquals(dto.cardTypeOrigin, "CREDIT")
        Assert.assertEquals(dto.issuedDay, "20200308")
        Assert.assertEquals(dto.expiresDay, "20500308")
        Assert.assertEquals(dto.cardStatus, CardStatus.REGISTERED)
        Assert.assertEquals(dto.cardStatusOrigin, "01")
        Assert.assertEquals(dto.lastUseDay, "20200202")
        Assert.assertEquals(dto.lastUseTime, "130303")
        Assert.assertEquals(dto.annualFee, BigDecimal(1000).setScale(4))
        Assert.assertEquals(dto.paymentBankId, "SHBANK")
        Assert.assertEquals(dto.paymentAccountNumber, "accountNumber")
        Assert.assertEquals(dto.isBusinessCard, false)
        Assert.assertEquals(dto.trafficSupported, false)
    }

    @Test
    @DisplayName("CardDto -> CardEntity 변환 테스트")
    fun dtoToEntityTest() {

        val dto = createCardDto()
        val entity = cardMapper.toCardEntity(dto)

        Assert.assertEquals(entity.cardId, null)
        Assert.assertEquals(entity.banksaladUserId, null)
        Assert.assertEquals(entity.cardCompanyId, "shinhancard")
        Assert.assertEquals(entity.cardCompanyCardId, "SHC")
        Assert.assertEquals(entity.lastCheckAt, null)
        Assert.assertEquals(entity.cardOwnerName, "홍길동")
        Assert.assertEquals(entity.cardOwnerType, "FAMILY")
        Assert.assertEquals(entity.cardOwnerTypeOrigin, "본인")
        Assert.assertEquals(entity.cardName, "나라사랑카드")
        Assert.assertEquals(entity.cardBrandName, "BC")
        Assert.assertEquals(entity.internationalBrandName, "SHINHANCARD")
        Assert.assertEquals(entity.cardNumber, "1234-5678-1234-5678")
        Assert.assertEquals(entity.cardNumberMask, "****-5678-1234-****")
        Assert.assertEquals(entity.cardType, CardType.CREDIT.name)
        Assert.assertEquals(entity.cardTypeOrigin, "CREDIT")
        Assert.assertEquals(entity.issuedDay, "20200308")
        Assert.assertEquals(entity.expirationDay, "20500308")
        Assert.assertEquals(entity.cardStatus, CardStatus.REGISTERED.name)
        Assert.assertEquals(entity.cardStatusOrigin, "01")
        Assert.assertEquals(entity.lastUseDay, "20200202")
        Assert.assertEquals(entity.lastUseTime, "130303")
        Assert.assertEquals(entity.annualFee, BigDecimal(1000).setScale(4))
        Assert.assertEquals(entity.paymentBankId, "SHBANK")
        Assert.assertEquals(entity.paymentAccountNumber, "accountNumber")
        Assert.assertEquals(entity.isBusinessCard, false)
        Assert.assertEquals(entity.createdAt, null)
        Assert.assertEquals(entity.updatedAt, null)
    }

    @Test
    @DisplayName("CardEntity -> CardHistoryEntity 변환 테스트")
    fun cardEntityToCardHistoryEntityTest() {
        val cardEntity = createCardEntity(LocalDateTime.now())
        val cardHistoryEntity = cardMapper.toCardHistoryEntity(cardEntity)

        Assert.assertEquals(cardHistoryEntity.cardHistoryId, null)
        Assert.assertEquals(cardHistoryEntity.cardId, cardEntity.cardId)
        Assert.assertEquals(cardHistoryEntity.banksaladUserId, cardEntity.banksaladUserId)
        Assert.assertEquals(cardHistoryEntity.cardCompanyId, cardEntity.cardCompanyId)
        Assert.assertEquals(cardHistoryEntity.lastCheckAt, cardEntity.lastCheckAt)
        Assert.assertEquals(cardHistoryEntity.cardOwnerName, cardEntity.cardOwnerName)
        Assert.assertEquals(cardHistoryEntity.cardOwnerType, cardEntity.cardOwnerType)
        Assert.assertEquals(cardHistoryEntity.cardOwnerTypeOrigin, cardEntity.cardOwnerTypeOrigin)
        Assert.assertEquals(cardHistoryEntity.cardName, cardEntity.cardName)
        Assert.assertEquals(cardHistoryEntity.cardBrandName, cardEntity.cardBrandName)
        Assert.assertEquals(cardHistoryEntity.internationalBrandName, cardEntity.internationalBrandName)
        Assert.assertEquals(cardHistoryEntity.cardNumber, cardEntity.cardNumber)
        Assert.assertEquals(cardHistoryEntity.cardNumberMask, cardEntity.cardNumberMask)
        Assert.assertEquals(cardHistoryEntity.cardType, cardEntity.cardType)
        Assert.assertEquals(cardHistoryEntity.cardTypeOrigin, cardEntity.cardTypeOrigin)
        Assert.assertEquals(cardHistoryEntity.issuedDay, cardEntity.issuedDay)
        Assert.assertEquals(cardHistoryEntity.expirationDay, cardEntity.expirationDay)
        Assert.assertEquals(cardHistoryEntity.cardStatus, cardEntity.cardStatus)
        Assert.assertEquals(cardHistoryEntity.cardStatusOrigin, cardEntity.cardStatusOrigin)
        Assert.assertEquals(cardHistoryEntity.lastUseDay, cardEntity.lastUseDay)
        Assert.assertEquals(cardHistoryEntity.lastUseTime, cardEntity.lastUseTime)
        Assert.assertEquals(cardHistoryEntity.annualFee, cardEntity.annualFee)
        Assert.assertEquals(cardHistoryEntity.paymentBankId, cardEntity.paymentBankId)
        Assert.assertEquals(cardHistoryEntity.paymentAccountNumber, cardEntity.paymentAccountNumber)
        Assert.assertEquals(cardHistoryEntity.isBusinessCard, cardEntity.isBusinessCard)
        Assert.assertEquals(cardHistoryEntity.createdAt, null)
        Assert.assertEquals(cardHistoryEntity.updatedAt, null)
    }

    @Test
    @DisplayName("CardDto -> CardEntity 병합 테스트")
    fun mergeTest() {
        val now = LocalDateTime.now()

        val dto = createCardDto()
        dto.annualFee = BigDecimal(20000).setScale(4)
        dto.cardStatus = CardStatus.TERMINATED
        dto.issuedDay = null
        dto.lastUseDay = null

        val entity = createCardEntity(now)

        cardMapper.merge(dto, entity)

        Assert.assertNotNull(entity.cardId)
        Assert.assertEquals(entity.cardId, 1L)
        Assert.assertNotNull(entity.banksaladUserId)
        Assert.assertEquals(entity.banksaladUserId, 12345L)
        Assert.assertEquals(entity.cardCompanyId, "shinhancard")
        Assert.assertEquals(entity.cardCompanyCardId, "SHC")
        Assert.assertEquals(entity.lastCheckAt, now)
        Assert.assertEquals(entity.cardOwnerName, "홍길동")
        Assert.assertEquals(entity.cardOwnerType, "FAMILY")
        Assert.assertEquals(entity.cardOwnerTypeOrigin, "본인")
        Assert.assertEquals(entity.cardName, "나라사랑카드")
        Assert.assertEquals(entity.cardBrandName, "BC")
        Assert.assertEquals(entity.internationalBrandName, "SHINHANCARD")
        Assert.assertEquals(entity.cardNumber, "1234-5678-1234-5678")
        Assert.assertEquals(entity.cardNumberMask, "****-5678-1234-****")
        Assert.assertEquals(entity.cardType, CardType.CREDIT.name)
        Assert.assertEquals(entity.cardTypeOrigin, "CREDIT")
        Assert.assertEquals(entity.issuedDay, null)
        Assert.assertEquals(entity.expirationDay, "20500308")
        Assert.assertEquals(entity.cardStatus, CardStatus.TERMINATED.name)
        Assert.assertEquals(entity.cardStatusOrigin, "01")
        Assert.assertEquals(entity.lastUseDay, null)
        Assert.assertEquals(entity.lastUseTime, "130303")
        Assert.assertEquals(entity.annualFee, BigDecimal(20000).setScale(4))
        Assert.assertEquals(entity.paymentBankId, "SHBANK")
        Assert.assertEquals(entity.paymentAccountNumber, "accountNumber")
        Assert.assertEquals(entity.isBusinessCard, false)
        Assert.assertNotNull(entity.createdAt)
        Assert.assertNotNull(entity.updatedAt)
        Assert.assertEquals(entity.createdAt, now)
        Assert.assertEquals(entity.updatedAt, now)
    }

    @Test
    @DisplayName("CardDto 비교 테스트")
    fun cardDtoEqualsTest() {
        val dto1 = createCardDto()
        val dto2 = createCardDto()

        Assert.assertEquals(dto1.unequals(dto2), false)

        dto2.cardName = "후불하이패스카드"

        Assert.assertEquals(dto1.unequals(dto2), true)
    }

    fun createCardEntity(now: LocalDateTime?): CardEntity {
        return CardEntity().apply {
            this.cardId = 1L
            this.banksaladUserId = 12345L
            this.cardCompanyId = "shinhancard"
            this.cardCompanyCardId = "SHC"
            this.lastCheckAt = now
            this.cardOwnerName = "홍길동"
            this.cardOwnerType = "FAMILY"
            this.cardOwnerTypeOrigin = "본인"
            this.cardName = "나라사랑카드"
            this.cardBrandName = "BC"
            this.internationalBrandName = "SHINHANCARD"
            this.cardNumber = "1234-5678-1234-5678"
            this.cardNumberMask = "****-5678-1234-****"
            this.cardType = CardType.CREDIT.name
            this.cardTypeOrigin = "CREDIT"
            this.issuedDay = "20200308"
            this.expirationDay = "20500308"
            this.cardStatus = CardStatus.REGISTERED.name
            this.cardStatusOrigin = "01"
            this.lastUseDay = "20200202"
            this.lastUseTime = "130303"
            this.annualFee = BigDecimal(1000).setScale(4)
            this.paymentBankId = "SHBANK"
            this.paymentAccountNumber = "accountNumber"
            this.isBusinessCard = false
            this.createdAt = now
            this.updatedAt = now
        }
    }

    fun createCardDto(): Card {
        return Card().apply {
            this.cardCompanyId = "shinhancard"
            this.cardCompanyCardId = "SHC"
            this.cardOwnerName = "홍길동"
            this.cardOwnerType = CardOwnerType.FAMILY
            this.cardOwnerTypeOrigin = "본인"
            this.cardName = "나라사랑카드"
            this.cardBrandName = "BC"
            this.internationalBrandName = "SHINHANCARD"
            this.cardNumber = "1234-5678-1234-5678"
            this.cardNumberMask = "****-5678-1234-****"
            this.cardType = CardType.CREDIT
            this.cardTypeOrigin = "CREDIT"
            this.issuedDay = "20200308"
            this.expiresDay = "20500308"
            this.cardStatus = CardStatus.REGISTERED
            this.cardStatusOrigin = "01"
            this.lastUseDay = "20200202"
            this.lastUseTime = "130303"
            this.annualFee = BigDecimal(1000).setScale(4)
            this.paymentBankId = "SHBANK"
            this.paymentAccountNumber = "accountNumber"
            this.isBusinessCard = false
            this.trafficSupported = false
        }
    }
}
