package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.cardbills.mapper.CardBillMapper
import com.rainist.collectcard.cardbills.mapper.CardBillScheduledMapper
import com.rainist.collectcard.cardbills.mapper.CardBillTransactionMapper
import com.rainist.collectcard.cardbills.mapper.CardPaymentScheduledMapper
import com.rainist.collectcard.common.db.repository.CardBillRepository
import com.rainist.collectcard.common.db.repository.CardBillScheduledRepository
import com.rainist.collectcard.common.db.repository.CardBillTransactionRepository
import com.rainist.collectcard.common.db.repository.CardPaymentScheduledRepository
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import com.rainist.collectcard.common.util.ReflectionCompareUtil
import com.rainist.common.log.Log
import java.time.LocalDateTime
import org.apache.commons.lang3.builder.EqualsBuilder
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class CardBillPublishService(
    val cardBillRepository: CardBillRepository,
    val cardBillTransactionRepository: CardBillTransactionRepository,
    val cardBillScheduledRepository: CardBillScheduledRepository,
    val cardPaymentScheduledRepository: CardPaymentScheduledRepository
) {
    val cardBillMapper = Mappers.getMapper(CardBillMapper::class.java)
    val cardBillTransactionMapper = Mappers.getMapper(CardBillTransactionMapper::class.java)
    val cardBillScheduledMapper = Mappers.getMapper(CardBillScheduledMapper::class.java)
    val cardPaymentScheduledMapper = Mappers.getMapper(CardPaymentScheduledMapper::class.java)

    companion object : Log

    fun sync(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO()
    }

    fun shadowing(
        banksaladUserId: Long,
        organizationId: String,
        lastCheckAt: LocalDateTime,
        executionRequestId: String,
        oldResponse: ListCardBillsResponse
    ): CollectShadowingResponse {
        val cardBills = makeCardBills(banksaladUserId, organizationId, lastCheckAt)
        val cardBillScheduled = makeCardBillScheduled(banksaladUserId, organizationId, lastCheckAt)

        val newCardBills = sortedCardBill(cardBills + cardBillScheduled)
        val oldCardBill = sortedCardBill(oldResponse.dataBody?.cardBills)

        val isShadowingDiff = unequals(oldCardBill, newCardBills)

        if (isShadowingDiff and (newCardBills.size == oldCardBill.size)) {
            val diffFieldMap = ReflectionCompareUtil.reflectionCompareBills(oldCardBill, newCardBills)
            logger.With("diff_field_map", diffFieldMap.toString())
        }

        return CollectShadowingResponse(
            banksaladUserId = banksaladUserId,
            organizationId = organizationId,
            lastCheckAt = lastCheckAt.toString(),
            executionRequestId = executionRequestId,
            isDiff = isShadowingDiff,
            executionName = "cardbills",
            oldList = oldCardBill,
            dbList = newCardBills
        )
    }

    fun makeCardBills(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime): List<CardBill> {
        return cardBillRepository.findAllByBanksaladUserIdAndCardCompanyIdAndLastCheckAt(
            banksaladUserId,
            organizationId,
            lastCheckAt
        ).map { billEntity ->
            val transactions =
                cardBillTransactionRepository.findAllByBanksaladUserIdAndCardCompanyIdAndBillNumberAndLastCheckAt(
                    banksaladUserId,
                    organizationId,
                    billEntity.billNumber,
                    lastCheckAt
                ).map {
                    cardBillTransactionMapper.toBillTransactionDto(it)
                }.toMutableList()
            cardBillMapper.toCardBillDto(billEntity).copy(transactions = transactions)
        }
    }

    fun makeCardBillScheduled(
        banksaladUserId: Long,
        organizationId: String,
        lastCheckAt: LocalDateTime
    ): List<CardBill> {
        return cardBillScheduledRepository.findAllByBanksaladUserIdAndCardCompanyIdAndLastCheckAt(
            banksaladUserId,
            organizationId,
            lastCheckAt
        ).map { billEntity ->
            val transactions =
                cardPaymentScheduledRepository.findAllByBanksaladUserIdAndCardCompanyIdAndBillNumberAndLastCheckAt(
                    banksaladUserId,
                    organizationId,
                    billEntity.billNumber,
                    lastCheckAt
                ).map {
                    cardPaymentScheduledMapper.toBillTransactionDto(it)
                }.toMutableList()
            cardBillScheduledMapper.toCardBilldDto(billEntity).copy(transactions = transactions)
        }
    }

    fun sortedCardBill(cardBill: List<CardBill>?): List<CardBill> {
        /*
            bill은 paymentDay 기준 descending, transaction은 approvalDay 기준 descending
            이유는 old 데이터와 같은 기준으로 sort를 하기 위함.
         */
        val sortedDescending = cardBill?.sortedByDescending { it.paymentDay }

        sortedDescending?.forEach { bill ->
            val sortedByDescending = bill.transactions?.sortedByDescending { t -> t.approvalDay }
            bill.transactions = sortedByDescending?.toMutableList() ?: mutableListOf()
        }

        return sortedDescending ?: emptyList()
    }

    private fun unequals(oldBill: List<CardBill>, newBill: List<CardBill>): Boolean {
        if (oldBill.size != newBill.size) {
            return true
        }
        for (i in oldBill.indices) {
            if (!EqualsBuilder.reflectionEquals(oldBill[i], newBill[i])) {
                return true
            }
        }
        return false
    }
}
