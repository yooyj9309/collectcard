package com.rainist.collectcard.plcc.cardtransactions

import com.github.banksalad.idl.apis.v1.card.CardProto
import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.github.banksalad.idl.apis.v1.plcc.PlccProto
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransaction
import com.rainist.collectcard.plcc.common.db.entity.PlccCardTransactionEntity
import com.rainist.common.util.DateTimeUtil
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
            entity.cardName = ""
            entity.cardNumber = ""
            entity.cardNumberMask = ""
            entity.amount = plccCardTransaction.amount
            entity.canceledAmount = if (plccCardTransaction.approvalCancelType == "1") plccCardTransaction.amount else null
            entity.discountAmount = plccCardTransaction.discountAmount
            entity.discountRate = plccCardTransaction.discountRate
            entity.partialCanceledAmount = null
            entity.tax = null
            entity.serviceChargeAmount = null
            entity.netSalesAmount = null
            entity.businessLicenseNumber = null
            entity.storeName = plccCardTransaction.storeName
            entity.storeNumber = plccCardTransaction.storeNumber
            entity.storeCategory = null
            entity.cardType = null
            entity.cardTypeOrigin = null
            entity.cardTransactionType = null // PLCC 현재는 국내만 적용 TODO 신용만 존재 하는지 확인
            entity.cardTransactionTypeOrigin = null
            entity.currencyCode = "KRW" // PLCC 현재는 국내만 적용
            entity.transactionCountry = "KOREA" // PLCC 현재는 국내만 적용
            entity.isInstallmentPayment = plccCardTransaction.isInstallmentPayment
            entity.installment = plccCardTransaction.installment
            entity.paymentDay = null
            entity.isOverseaUse = false // PLCC 현재는 국내만 적용
            entity.benefitCode = plccCardTransaction.serviceCode
            entity.benefitName = plccCardTransaction.serviceName
            entity.benefitType = plccCardTransaction.serviceType
            entity.lastCheckAt = now
        }
    }

    fun plccCardTransactionEntityToProto(plccCardTransactionEntity: PlccCardTransactionEntity): CollectcardProto.RewardsTransaction {
        val date = plccCardTransactionEntity.approvalDay?.let { DateTimeUtil.stringToLocalDate(it) }
        val time = plccCardTransactionEntity.approvalTime?.let { DateTimeUtil.stringToLocalTime(it) }
        val approvalMs = DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(LocalDateTime.of(date, time))
        val builder = CollectcardProto.RewardsTransaction.newBuilder()

        builder.serviceCode = plccCardTransactionEntity.benefitCode
        builder.serviceName = getRewardsBenefitType(plccCardTransactionEntity.benefitName)
        builder.serviceType = getRewardsPromotionType(plccCardTransactionEntity.benefitType)
        builder.approvedAtMs = approvalMs
        builder.approvalNumber = plccCardTransactionEntity.approvalNumber
        builder.amount2F = plccCardTransactionEntity.discountAmount?.toLong() ?: 0L
        builder.discountAmount2F = plccCardTransactionEntity.discountAmount?.toLong() ?: 0L
        builder.isInstallmentPayment = plccCardTransactionEntity.isInstallmentPayment ?: false
        builder.installment = plccCardTransactionEntity.installment ?: 0
        builder.affiliatedStoreName = plccCardTransactionEntity.storeName
        builder.affiliatedStoreNumber = plccCardTransactionEntity.storeNumber
        builder.approvalType = getApprovalType(plccCardTransactionEntity.cardTransactionType)

        return builder.build()
    }

    private fun getRewardsBenefitType(type: String?): PlccProto.RewardsType {
        return when (type) {
            "01" -> PlccProto.RewardsType.REWARDS_TYPE_CAFE_DISCOUNT
            "02" -> PlccProto.RewardsType.REWARDS_TYPE_DELIVERY_APP_DISCOUNT
            "03" -> PlccProto.RewardsType.REWARDS_TYPE_STREAMING_DISCOUNT
            else -> PlccProto.RewardsType.REWARDS_TYPE_UNKNOWN
        }
    }

    private fun getRewardsPromotionType(type: String?): PlccProto.RewardsServiceType {
        return when (type) {
            "01" -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_CHARGE_DISCOUNT
            "02" -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_POINT
            "03" -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_INSTALLMENT_REDUCT
            else -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_UNKNOWN
        }
    }

    private fun getApprovalType(type: String?): CardProto.CardTransactionApprovalStatus {
        return when (type) {
            "0" -> CardProto.CardTransactionApprovalStatus.CARD_TRANSACTION_APPROVAL_STATUS_APPROVED
            "1" -> CardProto.CardTransactionApprovalStatus.CARD_TRANSACTION_APPROVAL_STATUS_ENTIRELY_CANCELED
            else -> CardProto.CardTransactionApprovalStatus.CARD_TRANSACTION_APPROVAL_STATUS_UNKNOWN
        }
    }
}
