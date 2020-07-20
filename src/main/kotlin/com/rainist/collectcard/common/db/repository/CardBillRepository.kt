package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardBillEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardBillRepository : JpaRepository<CardBillEntity, Long> {
    fun findByBanksaladUserIdAndCardCompanyIdAndBillNumber(
        banksaladUserId: Long,
        cardCompanyId: String,
        billNumber: String
    ): CardBillEntity?
}
