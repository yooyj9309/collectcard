package com.rainist.collectcard.common.collect.execution

import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionKey
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardBillTransactionExpected
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardBills
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardCards
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardCreditLimit
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardLoan
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardTransactions
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardUserInfo
import com.rainist.collectcard.common.exception.CollectcardException

class Executions() {

    companion object {
        fun valueOf(businessType: BusinessType, organization: Organization, transaction: Transaction): Execution {
            return map[ExecutionKey.builder()
                .business(businessType.name)
                .organization(organization.name)
                .transaction(transaction.name)
                .build()] ?: throw CollectcardException("execution not found")
        }

        private val map = mapOf<ExecutionKey, Execution>(
            // 보유카드
            ExecutionKey.builder()
                .business(BusinessType.card.name)
                .organization(Organization.shinhancard.name)
                .transaction(Transaction.cards.name)
                .build() to cardShinhancardCards,

            // 승인내역
            ExecutionKey.builder()
                .business(BusinessType.card.name)
                .organization(Organization.shinhancard.name)
                .transaction(Transaction.cardTransaction.name)
                .build() to cardShinhancardTransactions,

            // 결제예정금액조회
            ExecutionKey.builder()
                .business(BusinessType.card.name)
                .organization(Organization.shinhancard.name)
                .transaction(Transaction.billTransactionExpected.name)
                .build() to cardShinhancardBillTransactionExpected,
            // 청구서
            ExecutionKey.builder()
                .business(BusinessType.card.name)
                .organization(Organization.shinhancard.name)
                .transaction(Transaction.cardbills.name)
                .build() to cardShinhancardBills,

            // 개인정보조회
            ExecutionKey.builder()
                .business(BusinessType.card.name)
                .organization(Organization.shinhancard.name)
                .transaction(Transaction.userInfo.name)
                .build() to cardShinhancardUserInfo,

            // 대출정보조회
            ExecutionKey.builder()
                .business(BusinessType.card.name)
                .organization(Organization.shinhancard.name)
                .transaction(Transaction.loan.name)
                .build() to cardShinhancardLoan,

                // 개인한도조회
            ExecutionKey.builder()
                .business(BusinessType.card.name)
                .organization(Organization.shinhancard.name)
                .transaction(Transaction.creditLimit.name)
                .build() to cardShinhancardCreditLimit
        )
    }
}
