package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsResponse
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest

interface PlccCardRewardsService {

    fun getPlccCardRewards(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    ): PlccCardRewardsResponse
}
