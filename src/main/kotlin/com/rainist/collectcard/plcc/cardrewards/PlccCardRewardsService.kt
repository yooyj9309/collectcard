package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest

interface PlccCardRewardsService {

    fun getPlccRewards(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    )
}
