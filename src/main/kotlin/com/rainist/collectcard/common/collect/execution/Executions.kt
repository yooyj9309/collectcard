package com.rainist.collectcard.common.collect.execution

import com.rainist.collect.common.dto.Api
import com.rainist.collect.common.dto.Execution
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardBills
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardCards
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardListUserCardBillsExpected
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardLoan
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardTransactions
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardUserInfo
import com.rainist.collectcard.common.exception.CollectcardException

class Executions() {

    companion object {
        fun valueOf(businessType: BusinessType, organization: Organization, transaction: Transaction): Execution {
            return map[Api.builder()
                .business(businessType.name)
                .agency(organization.name)
                .transaction(transaction.name)
                .build()] ?: throw CollectcardException("execution not found")
        }

        private val map = mapOf<Api, Execution>(
            // 보유카드
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cards.name)
                .build() to cardShinhancardCards,

            // 승인내역
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardTransaction.name)
                .build() to cardShinhancardTransactions,

            // 결제예정금액조회
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardBillsExpected.name)
                .build() to cardShinhancardListUserCardBillsExpected,
            // 청구서
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardbills.name)
                .build() to cardShinhancardBills,

            // 개인정보조회
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.userInfo.name)
                .build() to cardShinhancardUserInfo,

            // 대출정보조회
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.loan.name)
                .build() to cardShinhancardLoan
        )
    }
}
