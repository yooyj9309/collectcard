package com.rainist.collectcard.common.collect.execution.shinhancard

import com.rainist.collect.common.execution.Execution
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.exception.CollectExecutionExceptionHandler
import com.rainist.common.log.Log

class ShinhancardCreditLimitExecution {
    companion object : Log {

        // 개인한도조회 (SHC_HPG01730)
        val cardShinhancardCreditLimit =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_credit_limit)
                .to(CreditLimitResponse::class.java)
                .exceptionally { throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        Organization.shinhancard.name,
                        "cardShinhancardCreditLimit",
                        ShinhancardApis.card_shinhancard_credit_limit.id,
                        throwable
                    )
                }
                .build()
    }
}
