package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardRewardsSummaryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardRewardsSummaryRepository : JpaRepository<PlccCardRewardsSummaryEntity, Long> {

    fun findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
        banksaladUserId: Long,
        cardCompanyId: String,
        cardCompanyCardId: String,
        benefitYearMonth: String
    ): PlccCardRewardsSummaryEntity?
}
