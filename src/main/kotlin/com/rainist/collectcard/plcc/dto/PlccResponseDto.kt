package com.rainist.collectcard.plcc.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class PlccResponseDto(

    @JsonProperty("result")
    var plccResultDto: PlccResultDto? = null

) {

    fun success(): PlccResponseDto {
        this.plccResultDto = PlccResultDto().success()
        return this
    }
}
