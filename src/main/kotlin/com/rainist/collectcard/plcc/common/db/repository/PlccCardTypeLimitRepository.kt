package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardTypeLimitEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardTypeLimitRepository : JpaRepository<PlccCardTypeLimitEntity, Long> {

    // DB 업데이트할 때 하나의 컬럼만 찾기위해 benefitCode 사용 
    fun findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonthAndBenefitCode(
        banksaladUserId: Long,
        cardCompanyId: String,
        cardCompanyCardId: String,
        benefitYearMonth: String,
        benefitCode: String
    ): PlccCardTypeLimitEntity?

    // publish 위해서 해당 월 혜택한도 전부 조회
    fun findAllByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
        banksaladUserId: Long,
        cardCompanyId: String,
        cardCompanyCardId: String,
        benefitYearMonth: String
    ): List<PlccCardTypeLimitEntity>
}
