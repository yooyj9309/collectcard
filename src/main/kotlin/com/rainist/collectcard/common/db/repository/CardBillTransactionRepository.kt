package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardBillTransactionEntity
import java.time.LocalDateTime
import org.springframework.data.jpa.repository.JpaRepository

interface CardBillTransactionRepository : JpaRepository<CardBillTransactionEntity, Long> {
    fun findAllByBilledYearMonthAndBanksaladUserIdAndCardCompanyIdAndBillNumber(
        billedYearMonth: String,
        banksaladUserId: Long?,
        cardCompanyId: String?,
        billNumber: String?
    ): List<CardBillTransactionEntity>

    fun findAllByBanksaladUserIdAndCardCompanyIdAndBillNumberAndLastCheckAt(
        banksaladUserId: Long,
        cardCompanyId: String,
        billNumber: String?,
        lastCheckAt: LocalDateTime
    ): List<CardBillTransactionEntity>
}
