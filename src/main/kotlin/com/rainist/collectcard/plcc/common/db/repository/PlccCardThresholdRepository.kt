package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardThresholdRepository : JpaRepository<PlccCardThresholdEntity, Long> {
    fun findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
        banksaladUserId: Long,
        cardCompanyId: String,
        cardCompanyCardId: String,
        benefitYearMonth: String
    ): PlccCardThresholdEntity?
}
