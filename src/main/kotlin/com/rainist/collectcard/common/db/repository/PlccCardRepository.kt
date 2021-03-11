package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.PlccCardEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardRepository : JpaRepository<PlccCardEntity, Long> {
    fun findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(
        banksaladUserId: Long?,
        cardCompanyId: String?,
        cardCompanyCardId: String?
    ): PlccCardEntity?
}
