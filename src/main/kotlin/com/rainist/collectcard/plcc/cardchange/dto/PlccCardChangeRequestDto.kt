package com.rainist.collectcard.plcc.cardchange.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class PlccCardChangeRequestDto(
    var ci: String? = null,
    var cardNumberMask: String? = null,
    var cid: String? = null,
    var statusChangeTime: String? = null,
    var statusType: String? = null,
    var cardStatus: String? = null
)
