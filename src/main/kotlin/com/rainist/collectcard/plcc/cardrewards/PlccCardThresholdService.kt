package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest

interface PlccCardThresholdService {

    fun getPlccCardThreshold(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    )
}
