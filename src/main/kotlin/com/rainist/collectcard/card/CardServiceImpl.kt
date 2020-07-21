package com.rainist.collectcard.card

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.card.dto.ListCardsRequest
import com.rainist.collectcard.card.dto.ListCardsRequestDataBody
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.entity.CardEntity
import com.rainist.collectcard.common.db.entity.CardHistoryEntity
import com.rainist.collectcard.common.db.repository.CardHistoryRepository
import com.rainist.collectcard.common.db.repository.CardRepository
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.common.service.CardOrganization
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardServiceImpl(
    val collectExecutorService: CollectExecutorService,
    val cardRepository: CardRepository,
    val cardHistoryRepository: CardHistoryRepository,
    val headerService: HeaderService
) : CardService {

    companion object : Log

    @Transactional
    override fun listCards(banksaladUserId: String, organization: CardOrganization): ListCardsResponse {
        /* header */
        val header = headerService.makeHeader(banksaladUserId, organization)

        /* request body */
        val listCardsRequest = ListCardsRequest().apply {
            dataBody = ListCardsRequestDataBody()
        }

        /* Call API */
        val executionResponse: ExecutionResponse<ListCardsResponse> = collectExecutorService.execute(
            Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cards),
            ExecutionRequest.builder<ListCardsRequest>()
                .headers(header)
                .request(listCardsRequest)
                .build()
        )

        /* response error handling */
        // TODO : error handling
        if (!HttpStatus.valueOf(executionResponse.httpStatusCode).is2xxSuccessful) {
            throw CollectcardException("Resopnse status is not success")
        }

        val listCardsResponse = executionResponse.response

        /* convert type and format if necessary */
        listCardsResponse.dataBody?.cards?.forEach { card ->
            card.cardNumber = card.cardNumber?.replace("-", "")
        }

        /* Save to DB and return */
        listCardsResponse.dataBody?.cards?.forEach { card ->
            upsertCardAndCardHistory(banksaladUserId, organization.organizationId ?: "", card)
        }

        return executionResponse.response
    }

    private fun upsertCardAndCardHistory(banksaladUserId: String, cardCompanyId: String, card: Card) {
        /* 카드 조회 */
        var cardEntity = cardRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(
            banksaladUserId.toLong(),
            cardCompanyId,
            card.cardCompanyCardId ?: ""
        ) ?: CardEntity()

        /* update 여부 체크 */
        if (!isCardUpdated(card, cardEntity)) return

        /* update card */
        cardEntity.apply {
            this.banksaladUserId = banksaladUserId.toLong()
            this.cardCompanyId = cardCompanyId
            this.cardCompanyCardId = card.cardCompanyCardId
            this.lastCheckAt = DateTimeUtil.getLocalDateTime()
            this.cardOwnerName = card.cardOwnerName
            this.cardOwnerType = card.cardOwnerType?.name
            this.cardOwnerTypeOrigin = card.cardOwnerTypeOrigin
            this.cardName = card.cardName
            this.cardBrandName = card.cardBrandName
            this.internationalBrandName = card.internationalBrandName
            this.cardNumber = card.cardNumber
            this.cardNumberMask = card.cardNumberMask
            this.cardType = card.cardType
            this.cardTypeOrigin = card.cardTypeOrigin
            this.issuedDate = card.issuedDate
            this.expirationDate = card.expiresDate
            this.cardStatus = card.cardStatus?.name
            this.cardStatusOrigin = card.cardStatusOrigin
            this.lastUseDate = card.lastUseDate
            this.lastUseTime = card.lastUseTime
            this.annualFee = card.annualFee
            this.paymentBankId = card.paymentBankId
            this.paymentAccountNumber = card.paymentAccountNumber
            this.isBusinessCard = card.isBusinessCard
        }

        cardEntity = cardRepository.save(cardEntity)

        /* insert card_history */
        cardHistoryRepository.save(CardHistoryEntity(cardEntity))
    }

    private fun isCardUpdated(card: Card, cardEntity: CardEntity): Boolean {
        cardEntity.let {
            if (cardEntity.cardOwnerName != card.cardOwnerName) return true
            if (cardEntity.cardOwnerType != card.cardOwnerType?.name) return true
            if (cardEntity.cardOwnerTypeOrigin != card.cardOwnerTypeOrigin) return true
            if (cardEntity.cardName != card.cardName) return true
            if (cardEntity.cardBrandName != card.cardBrandName) return true
            if (cardEntity.internationalBrandName != card.internationalBrandName) return true
            if (cardEntity.cardNumber != card.cardNumber) return true
            if (cardEntity.cardNumberMask != card.cardNumberMask) return true
            if (cardEntity.cardType != card.cardType) return true
            if (cardEntity.cardTypeOrigin != card.cardTypeOrigin) return true
            if (cardEntity.issuedDate != card.issuedDate) return true
            if (cardEntity.expirationDate != card.expiresDate) return true
            if (cardEntity.cardStatus != card.cardStatus?.name) return true
            if (cardEntity.cardStatusOrigin != card.cardStatusOrigin) return true
            if (cardEntity.lastUseDate != card.lastUseDate) return true
            if (cardEntity.lastUseTime != card.lastUseTime) return true
            if (cardEntity.annualFee != card.annualFee) return true
            if (cardEntity.paymentBankId != card.paymentBankId) return true
            if (cardEntity.paymentAccountNumber != card.paymentAccountNumber) return true
            if (cardEntity.isBusinessCard != card.isBusinessCard) return true
        }
        return false
    }
}
