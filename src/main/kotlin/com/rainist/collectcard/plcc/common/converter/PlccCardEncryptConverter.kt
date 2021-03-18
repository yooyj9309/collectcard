package com.rainist.collectcard.plcc.common.converter

import com.rainist.collectcard.common.converter.AttributeEncryptConverter
import com.rainist.collectcard.common.service.KeyManagementService
import org.springframework.stereotype.Component

@Component
class PlccCardEncryptConverter(
    keyManagementService: KeyManagementService
) : AttributeEncryptConverter(keyManagementService, KeyManagementService.KeyAlias.plcc_card)
