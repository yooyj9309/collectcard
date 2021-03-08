package com.rainist.collectcard.plcc.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class PlccCardDto(

    val cardNumberMask: String,
    val cardType: String,
    val cardIssueStatus: String,
    val cid: String,

    // @field:JsonIgnore
    // val cardName: String,

    val internationalBrandName: String,

    // @field:JsonIgnore
    // val cardProductName: String,

    val cardApplicationDay: String,

    val issuedDay: String,

    val expiresYearMonth: String,

    val ownerCard: String

    // @field:JsonIgnore
    // val cardOwnerName: String
)
