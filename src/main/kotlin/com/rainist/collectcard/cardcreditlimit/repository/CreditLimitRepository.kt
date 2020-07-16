package com.rainist.collectcard.cardcreditlimit.repository

import com.rainist.collectcard.cardcreditlimit.entity.CreditLimitEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CreditLimitRepository : JpaRepository<CreditLimitEntity, Long> {
    fun findCreditLimitEntitiesByBanksaladUserIdAndCardCompanyId(banksaladUserId: Long, cardCompanyId: String): CreditLimitEntity
}
