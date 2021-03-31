package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.common.enums.CardTransactionType
import com.rainist.collectcard.plcc.common.db.entity.PlccCardTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardTransactionRepository : JpaRepository<PlccCardTransactionEntity, Long> {

    fun findByApprovalYearMonthAndBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndApprovalNumberAndApprovalDayAndApprovalTime(
        approvalYearMonth: String?,
        banksaladUserId: Long?,
        cardCompanyId: String?,
        cardCompanyCardId: String?,
        approvalNumber: String?,
        approvalDay: String?,
        approvalTime: String?
    ): PlccCardTransactionEntity?

    fun findByApprovalYearMonthAndBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndApprovalNumberAndApprovalDayAndApprovalTimeAndCardTransactionType(
        approvalYearMonth: String?,
        banksaladUserId: Long?,
        cardCompanyId: String?,
        cardCompanyCardId: String?,
        approvalNumber: String?,
        approvalDay: String?,
        approvalTime: String?,
        cardTransactionType: CardTransactionType?
    ): PlccCardTransactionEntity?

    fun findAllByApprovalYearMonthAndBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(
        approvalYearMonth: String?,
        banksaladUserId: Long?,
        cardCompanyId: String?,
        cardCompanyCardId: String?
    ): List<PlccCardTransactionEntity>
}
