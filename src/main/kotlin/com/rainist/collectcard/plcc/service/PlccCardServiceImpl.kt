package com.rainist.collectcard.plcc.service

import com.rainist.collectcard.grpc.client.PlccClientService
import com.rainist.collectcard.plcc.dto.PlccCardDto
import com.rainist.collectcard.plcc.dto.SyncType
import org.springframework.stereotype.Service

@Service
class PlccCardServiceImpl(
    val plccClientService: PlccClientService
) : PlccCardService {
    override fun issuePlccCard(organizationId: String, ci: String, cards: List<PlccCardDto>) {
        plccClientService.syncPlccsByCollectcardData(
            organizationId,
            ci,
            cards,
            SyncType.ISSUED
        )

        // save to db
    }
}
