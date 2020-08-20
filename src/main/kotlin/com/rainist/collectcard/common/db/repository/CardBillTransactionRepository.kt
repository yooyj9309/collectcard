package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardBillTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardBillTransactionRepository : JpaRepository<CardBillTransactionEntity, Long> {
    fun findAllByBilledYearMonthAndBanksaladUserIdAndCardCompanyCardIdAndBillNumber(
        billedYearMonth: String,
        banksaladUserId: Long?,
        cardCompanyId: String?,
        billNumber: String?
    ): List<CardBillTransactionEntity>?
}
