package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardLoanEntity
import java.time.LocalDateTime
import org.springframework.data.jpa.repository.JpaRepository

interface CardLoanRepository : JpaRepository<CardLoanEntity, Long> {
    fun findByBanksaladUserIdAndCardCompanyIdAndCardCompanyLoanId(
        banksaladUserId: Long,
        cardCompanyId: String?,
        cardCompanyLoanId: String?
    ): CardLoanEntity?

    fun findAllByBanksaladUserIdAndCardCompanyIdAndLastCheckAt(
        banksaladUserId: Long,
        cardCompanyCardId: String,
        lastCheckAt: LocalDateTime?
    ): List<CardLoanEntity>
}
