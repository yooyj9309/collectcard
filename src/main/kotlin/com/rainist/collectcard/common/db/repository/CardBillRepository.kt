package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardBillEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardBillRepository : JpaRepository<CardBillEntity, Long> {
    fun findByBanksaladUserIdAndCardCompanyIdAndBillNumberAndBillTypeAndCardType(
        banksaladUserId: Long,
        cardCompanyId: String,
        billNumber: String,
        billType: String,
        cardType: String
    ): CardBillEntity?
}
