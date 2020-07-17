package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CreditLimitEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CreditLimitRepository : JpaRepository<CreditLimitEntity, Long> {
    fun findCreditLimitEntitiesByBanksaladUserIdAndCardCompanyId(
        banksaladUserId: Long,
        cardCompanyId: String
    ): CreditLimitEntity?
}
