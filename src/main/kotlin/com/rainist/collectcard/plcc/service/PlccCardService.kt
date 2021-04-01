package com.rainist.collectcard.plcc.service

import com.rainist.collectcard.plcc.dto.PlccCardChangeRequestDto
import com.rainist.collectcard.plcc.dto.PlccCardDto
import java.time.LocalDateTime

interface PlccCardService {
    fun issuePlccCard(organizationId: String, ci: String, cards: List<PlccCardDto>, now: LocalDateTime)
    fun changePlccCard(organizationId: String, ci: PlccCardChangeRequestDto, now: LocalDateTime)
}
