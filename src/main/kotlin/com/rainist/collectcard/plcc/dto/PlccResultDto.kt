package com.rainist.collectcard.plcc.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class PlccResultDto(
    var code: String? = null,
    var message: String? = null
) {

    fun success(): PlccResultDto {
        this.code = "OK"
        this.message = "정상처리되었습니다."
        return this
    }
}
