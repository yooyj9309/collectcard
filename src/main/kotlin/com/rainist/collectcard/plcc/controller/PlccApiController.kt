package com.rainist.collectcard.plcc.controller

import com.rainist.collectcard.plcc.dto.PlccIssueCardRequestDto
import com.rainist.collectcard.plcc.dto.PlccResponseDto
import com.rainist.collectcard.plcc.service.PlccCardService
import com.rainist.common.log.Log
import com.rainist.common.service.ObjectMapperService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/plcc")
class PlccApiController(
    val plccCardService: PlccCardService,
    val objectMapperService: ObjectMapperService
) {

    companion object : Log

    @PostMapping(path = ["/lottecard/card/issue"])
    fun issue(entity: HttpEntity<String>): ResponseEntity<String> {

        val request = objectMapperService.toObject(entity.body.toString(), PlccIssueCardRequestDto::class.java)

        val cid = request?.cardList?.getOrNull(0)?.cid
        return ResponseEntity(objectMapperService.toJson(PlccResponseDto().success(cid)), HttpStatus.OK)
    }

    @GetMapping("/ping")
    fun ping(): String {
        return "pong"
    }

/*    private fun makeEuckrHeader(): HttpHeaders {
        val resHeader = HttpHeaders()
        resHeader.add("Content-Type", "application/json;charset=euc-kr")
        return resHeader
    }*/
}
