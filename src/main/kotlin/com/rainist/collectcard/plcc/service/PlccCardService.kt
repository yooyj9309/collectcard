package com.rainist.collectcard.plcc.service

import com.rainist.collectcard.plcc.dto.PlccCardDto

interface PlccCardService {
    fun issuePlccCard(organizationId: String, ci: String, cards: List<PlccCardDto>)
}
