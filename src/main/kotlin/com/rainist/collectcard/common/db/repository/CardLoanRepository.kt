package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardLoanEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardLoanRepository : JpaRepository<CardLoanEntity, Long> {
    fun findByBanksaladUserIdAndCardCompanyIdAndCardCompanyLoanId(
        banksaladUserId: Long,
        cardCompanyId: String?,
        cardCompanyLoanId: String?
    ): CardLoanEntity?
}
