package com.rainist.collectcard.common.converter

import com.rainist.collectcard.common.service.KeyManagementService
import org.springframework.stereotype.Component

@Component
class CardLoanEncryptConverter(
    keyManagementService: KeyManagementService
) : AttributeEncryptConverter(keyManagementService, KeyManagementService.KeyAlias.card_loan)
