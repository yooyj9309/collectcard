package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardTransactionRepository : JpaRepository<CardTransactionEntity, Long> {
    fun findByBanksaladUserIdAndAndCardCompanyIdAndCardCompanyCardIdAndApprovalNumberAndApprovalDateAndApprovalTime(
        banksaladUserId: Long,
        cardCompanyId: String?,
        cardCompanyCardId: String?,
        approvalNumber: String?,
        approvalDate: String?,
        approvalTime: String?
    ): CardTransactionEntity?
}
