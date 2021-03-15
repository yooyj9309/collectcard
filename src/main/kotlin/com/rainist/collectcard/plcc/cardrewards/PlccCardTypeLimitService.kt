package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardTypeLimit
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest

interface PlccCardTypeLimitService {

    fun listPlccCardTypeLimit(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    ): List<PlccCardTypeLimit>
}
