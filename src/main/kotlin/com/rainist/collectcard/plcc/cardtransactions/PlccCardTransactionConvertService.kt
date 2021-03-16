package com.rainist.collectcard.plcc.cardtransactions

import com.github.banksalad.idl.apis.v1.card.CardProto
import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.github.banksalad.idl.apis.v1.plcc.PlccProto
import com.rainist.collectcard.common.enums.CardOwnerType
import com.rainist.collectcard.common.enums.CardTransactionType
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransaction
import com.rainist.collectcard.plcc.cardtransactions.enums.PlccCardServiceType
import com.rainist.collectcard.plcc.common.db.entity.PlccCardTransactionEntity
import com.rainist.common.util.DateTimeUtil
import java.math.BigDecimal
import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class PlccCardTransactionConvertService {

    fun plccCardTransactionToEntity(plccCardTransaction: PlccCardTransaction, banksaladUserId: Long? = null, cardCompanyId: String? = null, cardCompanyCardId: String? = null, yearMonth: String?, now: LocalDateTime): PlccCardTransactionEntity {
        return PlccCardTransactionEntity().also { entity ->
            entity.approvalYearMonth = yearMonth
            entity.banksaladUserId = banksaladUserId
            entity.cardCompanyId = cardCompanyId
            entity.cardCompanyCardId = cardCompanyCardId
            entity.approvalNumber = plccCardTransaction.approvalNumber
            entity.approvalDay = plccCardTransaction.approvalDay
            entity.approvalTime = plccCardTransaction.approvalTime
            entity.cardName = plccCardTransaction.cardName
            entity.cardNumber = plccCardTransaction.cardNumber
            entity.cardNumberMask = plccCardTransaction.cardNumberMask
            entity.amount = if (plccCardTransaction.cardTransactionType == CardTransactionType.APPROVAL) plccCardTransaction.amount else null
            entity.canceledAmount = if (plccCardTransaction.cardTransactionType == CardTransactionType.APPROVAL_CANCEL) plccCardTransaction.amount else null
            entity.discountAmount = plccCardTransaction.discountAmount
            entity.discountRate = plccCardTransaction.discountRate
            entity.partialCanceledAmount = plccCardTransaction.partialCanceledAmount
            entity.tax = plccCardTransaction.tax
            entity.serviceChargeAmount = plccCardTransaction.serviceChargeAmount
            entity.netSalesAmount = plccCardTransaction.netSalesAmount
            entity.businessLicenseNumber = plccCardTransaction.businessLicenseNumber
            entity.storeName = plccCardTransaction.storeName
            entity.storeNumber = plccCardTransaction.storeNumber
            entity.storeCategory = plccCardTransaction.storeCategory
            entity.cardType = plccCardTransaction.cardType
            entity.cardTypeOrigin = plccCardTransaction.cardTypeOrigin
            entity.cardTransactionType = plccCardTransaction.cardTransactionType
            entity.cardTransactionTypeOrigin = plccCardTransaction.cardTransactionTypeOrigin
            entity.currencyCode = plccCardTransaction.currencyCode
            entity.transactionCountry = plccCardTransaction.transactionCountry
            entity.isInstallmentPayment = plccCardTransaction.isInstallmentPayment
            entity.installment = plccCardTransaction.installment
            entity.paymentDay = plccCardTransaction.paymentDay
            entity.isOverseaUse = plccCardTransaction.isOverseaUse
            entity.benefitCode = plccCardTransaction.serviceCode
            entity.benefitCodeOrigin = plccCardTransaction.serviceCodeOrigin
            entity.benefitName = plccCardTransaction.serviceName
            entity.serviceType = plccCardTransaction.serviceType
            entity.serviceTypeOrigin = plccCardTransaction.serviceTypeOrigin
            entity.cardOwnerType = plccCardTransaction.cardOwnerType
            entity.cardOwnerTypeOrigin = plccCardTransaction.cardOwnerTypeOrigin
            entity.lastCheckAt = now
        }
    }

    fun plccCardTransactionEntityToProto(plccCardTransactionEntity: PlccCardTransactionEntity): CollectcardProto.RewardsTransaction {
        val date = plccCardTransactionEntity.approvalDay?.let { DateTimeUtil.stringToLocalDate(it) }
        val time = plccCardTransactionEntity.approvalTime?.let { DateTimeUtil.stringToLocalTime(it) }
        val approvalMs = DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(LocalDateTime.of(date, time))
        val builder = CollectcardProto.RewardsTransaction.newBuilder()

        builder.serviceCode = plccCardTransactionEntity.benefitCode
        builder.serviceName = plccCardTransactionEntity.benefitName
        builder.serviceType = getRewardsPromotionType(plccCardTransactionEntity.serviceType)
        builder.approvedAtMs = approvalMs
        builder.approvalNumber = plccCardTransactionEntity.approvalNumber
        val amount = if (plccCardTransactionEntity.cardTransactionType == CardTransactionType.APPROVAL) plccCardTransactionEntity.amount else plccCardTransactionEntity.canceledAmount
        builder.amount2F = amount?.multiply(BigDecimal(100))?.toLong() ?: 0L
        builder.discountAmount2F = plccCardTransactionEntity.discountAmount?.multiply(BigDecimal(100))?.toLong() ?: 0L
        builder.isInstallmentPayment = plccCardTransactionEntity.isInstallmentPayment ?: false
        builder.installment = plccCardTransactionEntity.installment ?: 0
        builder.affiliatedStoreName = plccCardTransactionEntity.storeName
        builder.affiliatedStoreNumber = plccCardTransactionEntity.storeNumber
        builder.approvalType = getApprovalType(plccCardTransactionEntity.cardTransactionType)
        builder.cardOwnerType = getOwnerType(plccCardTransactionEntity.cardOwnerType)

        return builder.build()
    }

    private fun getOwnerType(ownerType: CardOwnerType?): CardProto.CardOwnerType {
        return when (ownerType) {
            CardOwnerType.SELF -> CardProto.CardOwnerType.CARD_OWNER_TYPE_OWNER
            CardOwnerType.FAMILY -> CardProto.CardOwnerType.CARD_OWNER_TYPE_FAMILY
            else -> CardProto.CardOwnerType.CARD_OWNER_TYPE_UNKNOWN
        }
    }

    private fun getRewardsBenefitType(type: String?): PlccProto.RewardsType {
        return when (type) {
            "01" -> PlccProto.RewardsType.REWARDS_TYPE_CAFE_DISCOUNT
            "02" -> PlccProto.RewardsType.REWARDS_TYPE_DELIVERY_APP_DISCOUNT
            "03" -> PlccProto.RewardsType.REWARDS_TYPE_STREAMING_DISCOUNT
            else -> PlccProto.RewardsType.REWARDS_TYPE_UNKNOWN
        }
    }

    private fun getRewardsPromotionType(type: PlccCardServiceType?): PlccProto.RewardsServiceType {
        return when (type) {
            PlccCardServiceType.REWARDS_SERVICE_TYPE_CHARGE_DISCOUNT -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_CHARGE_DISCOUNT
            PlccCardServiceType.REWARDS_SERVICE_TYPE_POINT -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_POINT
            PlccCardServiceType.REWARDS_SERVICE_TYPE_INSTALLMENT_REDUCT -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_INSTALLMENT_REDUCT
            else -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_UNKNOWN
        }
    }

    private fun getApprovalType(type: CardTransactionType?): CardProto.CardTransactionApprovalStatus {
        return when (type) {
            CardTransactionType.APPROVAL -> CardProto.CardTransactionApprovalStatus.CARD_TRANSACTION_APPROVAL_STATUS_APPROVED
            CardTransactionType.APPROVAL_CANCEL -> CardProto.CardTransactionApprovalStatus.CARD_TRANSACTION_APPROVAL_STATUS_ENTIRELY_CANCELED
            else -> CardProto.CardTransactionApprovalStatus.CARD_TRANSACTION_APPROVAL_STATUS_UNKNOWN
        }
    }
}
