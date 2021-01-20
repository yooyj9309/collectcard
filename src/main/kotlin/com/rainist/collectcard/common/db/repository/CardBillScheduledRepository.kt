package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardBillScheduledEntity
import java.time.LocalDateTime
import org.springframework.data.jpa.repository.JpaRepository

interface CardBillScheduledRepository : JpaRepository<CardBillScheduledEntity, Long> {
    fun findByBanksaladUserIdAndCardCompanyIdAndBillNumberAndBillTypeAndCardType(
        banksaladUserId: Long,
        cardCompanyId: String,
        billNumber: String,
        billType: String,
        cardType: String
    ): CardBillScheduledEntity?

    fun findAllByBanksaladUserIdAndCardCompanyIdAndLastCheckAt(
        banksaladUserId: Long,
        cardCompanyId: String,
        lastCheckAt: LocalDateTime
    ): List<CardBillScheduledEntity>
}
