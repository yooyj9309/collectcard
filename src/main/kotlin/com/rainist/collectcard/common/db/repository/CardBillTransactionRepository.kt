package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardBillTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardBillTransactionRepository : JpaRepository<CardBillTransactionEntity, Long> {
    fun findAllByBilledYearMonthAndBanksaladUserIdAndCardCompanyIdAndBillNumberAndIsDeleted(
        billedYearMonth: String,
        banksaladUserId: Long?,
        cardCompanyId: String?,
        billNumber: String?,
        isDeleted: Boolean
    ): List<CardBillTransactionEntity>?
}
