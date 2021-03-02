package com.rainist.collectcard.plcc.controller

import com.rainist.collectcard.plcc.dto.PlccResponseDto
import com.rainist.common.log.Log
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/plcc")
class PlccApiController {

    companion object : Log

    @PostMapping("/lottecard/card/issue")
    fun issue(entity: HttpEntity<String>): ResponseEntity<PlccResponseDto> {
        logger.Warn("PLCC lotte card issue : {}", entity.body)

        return ResponseEntity.ok(
            PlccResponseDto().success()
        )
    }

    @GetMapping("/ping")
    fun ping(): String {
        return "pong"
    }
}
