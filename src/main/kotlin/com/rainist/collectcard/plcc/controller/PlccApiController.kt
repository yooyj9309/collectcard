package com.rainist.collectcard.plcc.controller

import com.rainist.collectcard.common.service.EncodeService
import com.rainist.collectcard.plcc.dto.PlccCardChangeRequestDto
import com.rainist.collectcard.plcc.dto.PlccIssueCardRequestDto
import com.rainist.collectcard.plcc.dto.PlccResponseDto
import com.rainist.collectcard.plcc.service.PlccCardService
import com.rainist.common.log.Log
import com.rainist.common.service.ObjectMapperService
import com.rainist.common.util.DateTimeUtil
import java.nio.charset.Charset
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/plcc")
class PlccApiController(
    val plccCardService: PlccCardService,
    val objectMapperService: ObjectMapperService,
    val encodeService: EncodeService
) {

    companion object : Log

    @PostMapping(path = ["/lottecard/card/issue"])
    fun issue(@RequestBody plccIssueCardRequestDto: PlccIssueCardRequestDto): ResponseEntity<PlccResponseDto> {

        // TODO 테스트를 위해서 BASE64로 통신중 롯데카드 완료되면 수정 필요
        plccIssueCardRequestDto.cardList.forEach {
            it.ownerType = encodeService.base64Decode(it.ownerType, Charset.forName("MS949"))
            it.cardProductName = encodeService.base64Decode(it.cardProductName, Charset.forName("MS949"))
            it.cardOwnerName = encodeService.base64Decode(it.cardOwnerName, Charset.forName("MS949"))
        }

        plccCardService.issuePlccCard("lottecard", plccIssueCardRequestDto.ci, plccIssueCardRequestDto.cardList, DateTimeUtil.utcNowLocalDateTime())

        val cid = plccIssueCardRequestDto.cardList.getOrNull(0)?.cid

        return ResponseEntity(
            PlccResponseDto().success(cid, encodeService.base64Encode("정상처리 되었습니다", Charset.forName("MS949"))),
            HttpStatus.OK
        )
    }

    @PostMapping(path = ["/lottecard/card/change"])
    fun change(@RequestBody plccCardChangeRequestDto: PlccCardChangeRequestDto): ResponseEntity<PlccResponseDto> {

        plccCardService.changePlccCard(
            "lottecard",
            plccCardChangeRequestDto,
            DateTimeUtil.utcNowLocalDateTime()
        )

        val cid = plccCardChangeRequestDto.cid
        return ResponseEntity(
            PlccResponseDto().success(cid, encodeService.base64Encode("정상처리 되었습니다", Charset.forName("MS949"))),
            HttpStatus.OK
        )
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
