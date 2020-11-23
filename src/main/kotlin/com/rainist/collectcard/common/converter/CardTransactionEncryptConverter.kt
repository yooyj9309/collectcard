package com.rainist.collectcard.common.converter

import com.rainist.collectcard.common.service.KeyManagementService
import org.springframework.stereotype.Component

@Component
class CardTransactionEncryptConverter(
    keyManagementService: KeyManagementService
) : AttributeEncryptConverter(keyManagementService, KeyManagementService.KeyAlias.card_transaction)
