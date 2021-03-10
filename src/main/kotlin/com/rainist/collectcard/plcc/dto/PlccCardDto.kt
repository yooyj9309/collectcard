package com.rainist.collectcard.plcc.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class PlccCardDto(

    val cardNumberMask: String? = null,
    val cardType: String? = null,
    val cardIssueStatus: String? = null,
    val cid: String? = null,
    val cardName: String? = null,
    val internationalBrandName: String? = null,
    var cardProductName: String? = null,
    val cardApplicationDay: String? = null,
    val issuedDay: String? = null,
    val expiresYearMonth: String? = null,
    var ownerType: String? = null,
    var cardOwnerName: String? = null
)
