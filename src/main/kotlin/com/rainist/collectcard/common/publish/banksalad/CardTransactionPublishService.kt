package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.mapper.CardTransactionMapper
import com.rainist.collectcard.common.db.repository.CardTransactionRepository
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import com.rainist.common.log.Log
import java.time.LocalDateTime
import org.apache.commons.lang3.builder.EqualsBuilder
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class CardTransactionPublishService(
    val cardTransactionRepository: CardTransactionRepository
) {
    val transactionMapper = Mappers.getMapper(CardTransactionMapper::class.java)

    companion object : Log

    // TODO [FLOW] 해당부분을 제외하고 Diff 비교, 추후 필수로 추가필요.
    val EXCLUDE_EQUALS_FIELD = mutableListOf(
        CardTransaction::cardTransactionId.name,
        CardTransaction::paymentDay.name,
        CardTransaction::transactionCountry.name
    )

    fun sync(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO()
    }

    fun shadowing(
        banksaladUserId: Long,
        organizationId: String,
        lastCheckAt: LocalDateTime?,
        executionRequestId: String,
        oldResponse: ListTransactionsResponse
    ): CollectShadowingResponse {
        // Transaction은 lastCheckAt이 없어서 임시로 lastCheckAt 보다 이후에 적재된 내용을 조회
        val transactions = cardTransactionRepository.findAllByBanksaladUserIdAndCardCompanyIdAndLastCheckAt(
            banksaladUserId, organizationId, lastCheckAt
        ).map {
            transactionMapper.toTransactionDto(it)
        }.sortedWith(compareBy({ it.approvalNumber }, { it.cardNumber }))

        var oldTransactions =
            oldResponse.dataBody?.transactions?.sortedWith(compareBy({ it.approvalNumber }, { it.cardNumber }))
                ?: mutableListOf()

        oldTransactions.forEach {
            it.isInstallmentPayment = it.isInstallmentPayment ?: false
            it.amount = it.amount?.setScale(4)
            it.canceledAmount = it.canceledAmount?.setScale(4)
            it.partialCanceledAmount = it.partialCanceledAmount?.setScale(4)
            // installment를 넘겨주는 값은 golang에선 int32로 디폴트값 0
            it.installment = it.installment ?: 0
        }

        val isShadowingDiff = unequals(transactions, oldTransactions)

        if (isShadowingDiff) {
            for (i in oldTransactions.indices) {
                val isEqualObject = isEqualDiff(oldTransactions, i, transactions)
                if (!isEqualObject) {
                    logger.info("transaction diff index = {}", i)
                    logger.info("transaction diff old = {}", oldTransactions[i])
                    logger.info("transaction diff shadowing = {}", transactions[i])
                }
            }
        }

        return CollectShadowingResponse(
            banksaladUserId = banksaladUserId,
            organizationId = organizationId,
            lastCheckAt = lastCheckAt.toString(),
            executionRequestId = executionRequestId,
            isDiff = isShadowingDiff,
            executionName = "transaction",
            oldList = oldTransactions,
            dbList = transactions
        )
    }

    private fun unequals(transactions: List<CardTransaction>, oldTransactions: List<CardTransaction>): Boolean {
        if (transactions.size != oldTransactions.size)
            return true

        for (i in transactions.indices) {

            val rh = oldTransactions[i]
            val lh = transactions[i]
            if (!EqualsBuilder.reflectionEquals(transactions[i], oldTransactions[i], EXCLUDE_EQUALS_FIELD)) {
                return true
            }
        }
        return false
    }

    private fun isEqualDiff(
        oldTransactions: List<CardTransaction>,
        i: Int,
        transactions: List<CardTransaction>
    ): Boolean {
        return listOf(
            oldTransactions[i].cardTransactionId == transactions[i].cardTransactionId,
            oldTransactions[i].cardName == transactions[i].cardName,
            oldTransactions[i].cardNumber == transactions[i].cardNumber,
            oldTransactions[i].cardNumberMask == transactions[i].cardNumberMask,
            oldTransactions[i].cardCompanyCardId == transactions[i].cardCompanyCardId,
            oldTransactions[i].businessLicenseNumber == transactions[i].businessLicenseNumber,
            oldTransactions[i].storeName == transactions[i].storeName,
            oldTransactions[i].storeNumber == transactions[i].storeNumber,
            oldTransactions[i].cardType == transactions[i].cardType,
            oldTransactions[i].cardTypeOrigin == transactions[i].cardTypeOrigin,
            oldTransactions[i].cardTransactionType == transactions[i].cardTransactionType,
            oldTransactions[i].cardTransactionTypeOrigin == transactions[i].cardTransactionTypeOrigin,
            oldTransactions[i].currencyCode == transactions[i].currencyCode,
            oldTransactions[i].isInstallmentPayment == transactions[i].isInstallmentPayment,
            oldTransactions[i].installment == transactions[i].installment,
            oldTransactions[i].installmentRound == transactions[i].installmentRound,
            oldTransactions[i].netSalesAmount == transactions[i].netSalesAmount,
            oldTransactions[i].serviceChargeAmount == transactions[i].serviceChargeAmount,
            oldTransactions[i].tax == transactions[i].tax,
            oldTransactions[i].paidPoints == transactions[i].paidPoints,
            oldTransactions[i].isPointPay == transactions[i].isPointPay,
            oldTransactions[i].discountAmount == transactions[i].discountAmount,
            oldTransactions[i].amount == transactions[i].amount,
            oldTransactions[i].canceledAmount == transactions[i].canceledAmount,
            oldTransactions[i].partialCanceledAmount == transactions[i].partialCanceledAmount,
            oldTransactions[i].approvalNumber == transactions[i].approvalNumber,
            oldTransactions[i].approvalDay == transactions[i].approvalDay,
            oldTransactions[i].approvalTime == transactions[i].approvalTime,
            oldTransactions[i].pointsToEarn == transactions[i].pointsToEarn,
            oldTransactions[i].isOverseaUse == transactions[i].isOverseaUse,
            oldTransactions[i].paymentDay == transactions[i].paymentDay,
            oldTransactions[i].storeCategory == transactions[i].storeCategory,
            oldTransactions[i].transactionCountry == transactions[i].transactionCountry
        ).all { it }
    }
}
