package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardTypeLimitEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardTypeLimitRepository : JpaRepository<PlccCardTypeLimitEntity, Long> {
    fun findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonthAndBenefitCode(
        banksaladUserId: Long,
        cardCompanyId: String,
        cardCompanyCardId: String,
        benefitYearMonth: String,
        benefitCode: String
    ): PlccCardTypeLimitEntity?
}
