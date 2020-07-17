package com.rainist.collectcard.cardtransactions.repository

import com.rainist.collectcard.cardtransactions.entity.CardTransactionEntity
import java.time.LocalDateTime
import org.springframework.data.jpa.repository.JpaRepository

interface CardTransactionRepository : JpaRepository<CardTransactionEntity, Long> {
    fun findByBanksaladUserIdAndAndCardCompanyIdAndCardCompanyCardIdAndApprovalNumberAndIssuedDate(
        banksaladUserId: Long,
        cardCompanyId: String?,
        cardCompanyCardId: String?,
        approvalNumber: String?,
        issuedDate: LocalDateTime?
    ): CardTransactionEntity?
}
