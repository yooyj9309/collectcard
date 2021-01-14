package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardBillScheduledEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardBillScheduledRepository : JpaRepository<CardBillScheduledEntity, Long> {
    fun findByBanksaladUserIdAndCardCompanyIdAndBillNumberAndBillTypeAndCardType(
        banksaladUserId: Long,
        cardCompanyId: String,
        billNumber: String,
        billType: String,
        cardType: String
    ): CardBillScheduledEntity?
}
