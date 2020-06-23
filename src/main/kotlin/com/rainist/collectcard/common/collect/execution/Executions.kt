package com.rainist.collectcard.common.collect.execution

import com.rainist.collect.common.dto.Api
import com.rainist.collect.common.dto.Execution
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardCards
import com.rainist.collectcard.common.collect.execution.ShinhancardExecutions.Companion.cardShinhancardTransactions

class Executions() {

    companion object {
        fun valueOf(businessType: BusinessType, organization: Organization, transaction: Transaction): Execution {
            return map[Api.builder()
                .business(businessType.name)
                .agency(organization.name)
                .transaction(transaction.name)
                .build()] ?: throw RuntimeException()
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
                .build() to cardShinhancardTransactions

        )
    }
}
