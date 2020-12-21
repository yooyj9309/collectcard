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
