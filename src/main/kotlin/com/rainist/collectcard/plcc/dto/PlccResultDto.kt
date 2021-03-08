package com.rainist.collectcard.plcc.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class PlccResultDto(
    var message: String? = null,
    var code: String? = null
) {

    fun success(): PlccResultDto {
        this.message = "정상처리되었습니다."
        this.code = "OK"
        return this
    }

    fun success(cid: String?): PlccResultDto {
        this.message = "정상처리되었습니다."
        this.code = "OK"
        return this
    }
}
