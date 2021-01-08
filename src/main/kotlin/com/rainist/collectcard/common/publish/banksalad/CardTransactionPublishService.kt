package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.mapper.CardTransactionMapper
import com.rainist.collectcard.common.db.repository.CardTransactionRepository
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import java.time.LocalDateTime
import org.apache.commons.lang3.builder.EqualsBuilder
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class CardTransactionPublishService(
    val cardTransactionRepository: CardTransactionRepository
) {
    val transactionMapper = Mappers.getMapper(CardTransactionMapper::class.java)

    // TODO [FLOW] 해당부분을 제외하고 Diff 비교, 추후 필수로 추가필요.
    val EXCLUDE_EQUALS_FIELD = mutableListOf(
        CardTransaction::cardTransactionId.name,
        CardTransaction::paymentDay.name,
        CardTransaction::transactionCountry.name
    )

    fun sync(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO()
    }

    fun shadowing(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime?, executionRequestId: String, oldResponse: ListTransactionsResponse): CollectShadowingResponse {
        // Transaction은 lastCheckAt이 없어서 임시로 lastCheckAt 보다 이후에 적재된 내용을 조회
        val transactions = cardTransactionRepository.findAllByBanksaladUserIdAndCardCompanyIdAndCreatedAtGreaterThan(
            banksaladUserId, organizationId, lastCheckAt
        ).map {
            transactionMapper.toTransactionDto(it)
        }.sortedWith(compareBy({ it.approvalNumber }, { it.cardNumber }))

        var oldTransactions = oldResponse.dataBody?.transactions?.sortedWith(compareBy({ it.approvalNumber }, { it.cardNumber })) ?: mutableListOf()

        // 현재 null로 내려주는 부분이 있으며.
        oldTransactions.forEach {
            it.isInstallmentPayment = it.isInstallmentPayment ?: false
            it.amount = it.amount?.setScale(4)
            it.canceledAmount = it.canceledAmount?.setScale(4)
            it.partialCanceledAmount = it.partialCanceledAmount?.setScale(4)
        }

        val isShadowingDiff = unequals(transactions, oldTransactions)

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
            // installment를 넘겨주는 값은 golang에선 int32로 디폴트값 0
            oldTransactions[i].installment == oldTransactions[i].installment ?: 0
            if (!EqualsBuilder.reflectionEquals(transactions[i], oldTransactions[i], EXCLUDE_EQUALS_FIELD)) {
                return true
            }
        }
        return false
    }
}
