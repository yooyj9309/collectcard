package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardTransactionBenefitSummaryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardTransactionBenefitSummaryRepository : JpaRepository<PlccCardTransactionBenefitSummaryEntity, Long> {

    fun findByApprovalYearMonthAndBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(
        approvalYearMonth: String?,
        banksaladUserId: Long?,
        cardCompanyId: String?,
        cardCompanyCardId: String?
    ): PlccCardTransactionBenefitSummaryEntity?
}
