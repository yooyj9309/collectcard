package com.rainist.collectcard.plcc.controller

import com.rainist.collectcard.plcc.dto.PlccIssueCardRequestDto
import com.rainist.collectcard.plcc.dto.PlccResponseDto
import com.rainist.collectcard.plcc.service.PlccCardService
import com.rainist.common.log.Log
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/plcc")
class PlccApiController(
    val plccCardService: PlccCardService
) {

    companion object : Log

    @PostMapping("/lottecard/card/issue")
    fun issue(@RequestBody request: PlccIssueCardRequestDto): ResponseEntity<PlccResponseDto> {
        plccCardService.issuePlccCard("lottecard", request.ci, request.cardList)

        return ResponseEntity.ok(
            PlccResponseDto().success()
        )
    }

    @GetMapping("/ping")
    fun ping(): String {
        return "pong"
    }
}
