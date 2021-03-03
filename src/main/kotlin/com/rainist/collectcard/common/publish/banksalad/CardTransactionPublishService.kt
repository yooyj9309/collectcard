package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.mapper.CardTransactionMapper
import com.rainist.collectcard.common.db.repository.CardTransactionRepository
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import com.rainist.collectcard.common.util.ReflectionCompareUtil
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
    // val EXCLUDE_EQUALS_FIELD = mutableListOf(
    //     CardTransaction::cardTransactionId.name,
    //     CardTransaction::paymentDay.name,
    //     CardTransaction::transactionCountry.name
    // )

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
        val transactions = cardTransactionRepository.findAllByBanksaladUserIdAndCardCompanyIdAndLastCheckAt(
            banksaladUserId, organizationId, lastCheckAt
        ).map {
            transactionMapper.toTransactionDto(it)
        }.sortedWith(compareBy({ it.approvalNumber }, { it.cardNumber }))

        val oldTransactions =
            oldResponse.dataBody?.transactions?.sortedWith(compareBy({ it.approvalNumber }, { it.cardNumber }))
                ?: mutableListOf()

        val isShadowingDiff = unequals(transactions, oldTransactions)

        /**
         * isShadowingDiff = true인 경우, reflection 이용해 List<CardTransaction> 비교
         * 두 리스트의 사이즈가 다른 경우 reflectionCompare 동작하지 않는다. NPE 발생가능성
         */
        if (isShadowingDiff and (oldTransactions.size == transactions.size)) {
            val diffFieldMap = ReflectionCompareUtil.reflectionCompareCardTransaction(oldTransactions, transactions)
            logger.With("diff_field_map", diffFieldMap.toString())
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
            if (!EqualsBuilder.reflectionEquals(transactions[i], oldTransactions[i])) {
                return true
            }
        }
        return false
    }
}
