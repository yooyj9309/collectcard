package com.rainist.collectcard.plcc.cardrewards

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest

interface PlccCardRewardsPublishService {

    fun rewardsThresholdPublish(
        executionContext: CollectExecutionContext,
        request: PlccRpcRequest
    ): CollectcardProto.GetPlccRewardsThresholdResponse

    fun rewardsTypeLimitPublish(
        executionContext: CollectExecutionContext,
        request: PlccRpcRequest
    ): CollectcardProto.ListPlccRewardsTypeLimitResponse
}
