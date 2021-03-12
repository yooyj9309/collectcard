package com.rainist.collectcard.plcc.service

import com.rainist.collectcard.common.db.entity.PlccCardEntity
import com.rainist.collectcard.common.db.entity.PlccCardHistoryEntity
import com.rainist.collectcard.common.db.repository.PlccCardHistoryRepository
import com.rainist.collectcard.common.db.repository.PlccCardRepository
import com.rainist.collectcard.grpc.client.PlccClientService
import com.rainist.collectcard.grpc.client.UserV2ClientService
import com.rainist.collectcard.plcc.dto.PlccCardDto
import com.rainist.collectcard.plcc.dto.SyncType
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PlccCardServiceImpl(
    val userV2ClientService: UserV2ClientService,
    val plccClientService: PlccClientService,
    val plccCardRepository: PlccCardRepository,
    val plccCardHistoryRepository: PlccCardHistoryRepository
) : PlccCardService {

    @Value("\${lottecard.objectId}")
    private lateinit var lottecardOrganizationObjectId: String

    override fun issuePlccCard(organizationId: String, ci: String, cards: List<PlccCardDto>, now: LocalDateTime) {
        val user = userV2ClientService.getUserByCi(ci)
        if (user != null) {
            cards.forEach { card ->
                upsertPlccCardAndHistory(organizationId, user.userId.toLong(), card, now)
            }
        }

        plccClientService.syncPlccsByCollectcardData(
            lottecardOrganizationObjectId,
            ci,
            cards,
            SyncType.ISSUED
        )
    }

    private fun upsertPlccCardAndHistory(organizationId: String, userId: Long, cardDto: PlccCardDto, now: LocalDateTime) {
        val newEntity = cardDtoToEntity(organizationId, userId, cardDto, now)

        val oldEntity = plccCardRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(
            userId,
            organizationId,
            cardDto.cid
        )
        if (oldEntity != null) {
            updatePlccCard(oldEntity, newEntity)
            return
        }
        insertPlccCard(newEntity)
    }

    private fun updatePlccCard(oldEntity: PlccCardEntity, newEntity: PlccCardEntity) {
//        TODO(sangmin): 개발 중 history 쌓기 위해 무조건 update, 테스트 완료 후 equal 로직 추가
//        if (oldEntity == newEntity) {
//            return
//        }
        newEntity.apply {
            plccCardId = oldEntity.plccCardId
            createdAt = oldEntity.createdAt
            updatedAt = oldEntity.updatedAt
        }.let {
            plccCardRepository.save(it)
            plccCardHistoryRepository.save(
                plccCardEntityToPlccCardHistoryEntity(it)
            )
        }
    }

    private fun insertPlccCard(cardEntity: PlccCardEntity) {
        plccCardRepository.save(cardEntity)
        plccCardHistoryRepository.save(
            plccCardEntityToPlccCardHistoryEntity(cardEntity)
        )
    }

    private fun cardDtoToEntity(organizationId: String, userId: Long, card: PlccCardDto, now: LocalDateTime): PlccCardEntity {
        return PlccCardEntity().apply {
            this.banksaladUserId = userId
            this.cardCompanyId = organizationId
            this.cardCompanyCardId = card.cid
            this.lastCheckAt = now
//                this.cardOwnerName = card.cardOwnerName
            this.cardOwnerType = card.ownerType
            this.cardOwnerTypeOrigin = card.ownerType
//                this.cardName = card.cardName
//                this.cardBrandName =
            this.internationalBrandName = card.internationalBrandName
            this.cardNumber = card.cardNumberMask
            this.cardNumberMask = card.cardNumberMask
            this.cardType = card.cardType
            this.cardTypeOrigin = card.cardType
//                this.cardApplicationDay =
            this.issuedDay = card.issuedDay
            this.expirationDay = card.expiresYearMonth
            this.cardStatus = card.cardIssueStatus
            this.cardStatusOrigin = card.cardIssueStatus
//                this.lastUseDay =
//                this.lastUseTime =
//                this.annualFee =
//                this.paymentBankId =
//                this.paymentAccountNumber =
//                this.isBusinessCard =
        }
    }

    private fun plccCardEntityToPlccCardHistoryEntity(cardEntity: PlccCardEntity): PlccCardHistoryEntity {
        return PlccCardHistoryEntity().apply {
            this.plccCardId = cardEntity.plccCardId
            this.banksaladUserId = cardEntity.banksaladUserId
            this.cardCompanyId = cardEntity.cardCompanyId
            this.cardCompanyCardId = cardEntity.cardCompanyCardId
            this.lastCheckAt = cardEntity.lastCheckAt
            this.cardOwnerName = cardEntity.cardOwnerName
            this.cardOwnerType = cardEntity.cardOwnerType
            this.cardOwnerTypeOrigin = cardEntity.cardOwnerTypeOrigin
            this.cardName = cardEntity.cardName
            this.cardBrandName = cardEntity.cardBrandName
            this.internationalBrandName = cardEntity.internationalBrandName
            this.cardNumber = cardEntity.cardNumber
            this.cardNumberMask = cardEntity.cardNumberMask
            this.cardType = cardEntity.cardType
            this.cardTypeOrigin = cardEntity.cardTypeOrigin
            this.cardApplicationDay = cardEntity.cardApplicationDay
            this.issuedDay = cardEntity.issuedDay
            this.expirationDay = cardEntity.expirationDay
            this.cardStatus = cardEntity.cardStatus
            this.cardStatusOrigin = cardEntity.cardStatusOrigin
            this.lastUseDay = cardEntity.lastUseDay
            this.lastUseTime = cardEntity.lastUseTime
            this.annualFee = cardEntity.annualFee
            this.paymentBankId = cardEntity.paymentBankId
            this.paymentAccountNumber = cardEntity.paymentAccountNumber
            this.isBusinessCard = cardEntity.isBusinessCard
            this.isTrafficSupported = cardEntity.isTrafficSupported
        }
    }
}
