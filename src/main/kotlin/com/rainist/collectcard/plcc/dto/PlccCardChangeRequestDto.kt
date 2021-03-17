package com.rainist.collectcard.plcc.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class PlccCardChangeRequestDto(
    var ci: String,
    var cardNumberMask: String,
    var cid: String,
    var statusChangeTime: String,
    var statusType: String,
    var cardStatus: String
)
