package com.rainist.collectcard.cardloans.repository

import com.rainist.collectcard.cardloans.entity.CardLoanEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardLoanRepository : JpaRepository<CardLoanEntity, Long> {
    fun findByBanksaladUserIdAndCardCompanyIdAndCardCompanyLoanId(
        banksaladUserId: Long,
        cardCompanyId: String,
        cardCompanyLoanId: Long
    ): CardLoanEntity
}
