package com.rainist.collectcard.common.dto

data class CollectShadowingResponse(
    val banksaladUserId: Long,
    val organizationId: String,
    val lastCheckAt: String,
    val executionRequestId: String,
    val isDiff: Boolean,
    val executionName: String,
    // 기존 플로우로 받아오는 List.
    val oldList: List<Any>,
    // DB에서 꺼낸 List.
    val dbList: List<Any>
)

// Dto 리턴타입이 List가 아닌 하나의 객체인 경우 사용
data class SingleCollectShadowingResponse(
    val banksaladUserId: Long,
    val organizationId: String,
    val lastCheckAt: String,
    val executionRequestId: String,
    val isDiff: Boolean,
    val executionName: String,
    // 기존 response
    val oldResponse: Any,
    // DB에서 꺼낸 response
    val shadowingResponse: Any
)
