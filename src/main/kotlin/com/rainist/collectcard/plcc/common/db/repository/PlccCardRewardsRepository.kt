package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardRewardsEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardRewardsRepository : JpaRepository<PlccCardRewardsEntity, Long> {

    // DB 업데이트할 때 하나의 컬럼만 찾기위해 benefitCode 사용 
    fun findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonthAndBenefitCode(
        banksaladUserId: Long,
        cardCompanyId: String,
        cardCompanyCardId: String,
        benefitYearMonth: String,
        benefitCode: String
    ): PlccCardRewardsEntity?

    // publish 위해서 해당 월 혜택한도 전부 조회
    fun findAllByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
        banksaladUserId: Long,
        cardCompanyId: String,
        cardCompanyCardId: String,
        benefitYearMonth: String
    ): List<PlccCardRewardsEntity>
}
