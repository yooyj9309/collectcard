package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardPaymentScheduledEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CardPaymentScheduledRepository : JpaRepository<CardPaymentScheduledEntity, Long> {

    @Modifying
    @Query("delete from CardPaymentScheduledEntity t where t.banksaladUserId = ?1 and t.cardCompanyId = ?2")
    fun deleteAllByBanksaladUserIdAndCardCompanyId(
        banksaladUserId: Long?,
        cardCompanyId: String?
    )
}
