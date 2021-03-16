package com.rainist.collectcard.plcc.service

import com.rainist.collectcard.plcc.dto.PlccCardDto
import java.time.LocalDateTime

interface PlccCardService {
    fun issuePlccCard(organizationId: String, ci: String, cards: List<PlccCardDto>, now: LocalDateTime)
    fun changePlccCard(organizationId: String, ci: String, cardNumberMask: String, cid: String, statusType: String, cardStatus: String)
}
