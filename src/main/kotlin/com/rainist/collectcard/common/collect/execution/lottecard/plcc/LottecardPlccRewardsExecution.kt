package com.rainist.collectcard.common.collect.execution.lottecard.plcc

import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.common.collect.api.LottecardPlccApis
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.exception.CollectExecutionExceptionHandler
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsResponse

class LottecardPlccRewardsExecution {

    companion object {
        val lottecardPlccRewards =
            Execution.create()
                .exchange(LottecardPlccApis.card_lottecard_plcc_rewards)
                .to(PlccCardRewardsResponse::class.java)
                .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        executionContext,
                        Organization.lottecard.name,
                        "LottecardPlccRewards",
                        LottecardPlccApis.card_lottecard_plcc_rewards.id,
                        throwable
                    )
                }
                .build()
    }
}
