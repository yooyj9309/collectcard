package com.rainist.collectcard.grpc.client

import com.github.banksalad.idl.apis.v1.card.CardProto
import com.github.banksalad.idl.apis.v1.plcc.PlccGrpc
import com.github.banksalad.idl.apis.v1.plcc.PlccProto
import com.google.protobuf.StringValue
import com.rainist.collectcard.plcc.dto.PlccCardDto
import com.rainist.collectcard.plcc.dto.SyncType
import com.rainist.common.util.DateTimeUtil
import java.time.ZoneOffset
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("production", "development", "staging", "local")
class PlccClientService(
    @Qualifier("plccBlockingStub")
    val plccBlockingStub: PlccGrpc.PlccBlockingStub
) {

    fun syncPlccsByCollectcardData(
        organizationId: String,
        ci: String,
        data: List<PlccCardDto>,
        syncType: SyncType
    ): PlccProto.SyncPlccsByCollectcardDataResponse? {
        val request = PlccProto.SyncPlccsByCollectcardDataRequest.newBuilder()
            .setOrganizationId(organizationId)
            .setCi(ci)
            .addAllData(
                data.map {
                    PlccProto.CollectcardPlccData.newBuilder()
                        .setName(it.cardName)
                        .setNumber(it.cardNumberMask)
                        .setProductName(StringValue.of(it.cardProductName))
                        .setInternationalBrand(CardProto.CardInternationalBrand.valueOf(it.internationalBrandName ?: "UNKNOWN"))
                        .setOwnerName(StringValue.of(it.cardOwnerName))
//                        .setExternalState(StringValue.of(parseExternalState()))
                        .setStatus(cardIssueStatusToProtoEnum(it.cardIssueStatus))
                        .setIssuedAtMs(DateTimeUtil.stringDateTimeToEpochMilliSecond(it.issuedDay, "yyyyMMdd", ZoneOffset.UTC))
                        .setExpiresAtMs(DateTimeUtil.stringDateTimeToEpochMilliSecond(it.expiresYearMonth, "yyyyMM", ZoneOffset.UTC))
                        .build()
                }
            )
            .setSyncType(syncTypetoProtoEnum(syncType))
            .build()
        return plccBlockingStub.syncPlccsByCollectcardData(request)
    }

    private fun syncTypetoProtoEnum(syncType: SyncType): PlccProto.SyncUserPlccType {
        return when (syncType) {
            SyncType.ISSUED -> PlccProto.SyncUserPlccType.SYNC_PLCC_TYPE_ISSUED
            SyncType.STATUS_UPDATED -> PlccProto.SyncUserPlccType.SYNC_PLCC_TYPE_STATUS_UPDATED
            else -> PlccProto.SyncUserPlccType.SYNC_PLCC_TYPE_UNKNOWN
        }
    }

    private fun cardIssueStatusToProtoEnum(statusCode: String): CardProto.CardStatus {
        return when (statusCode) {
            "00" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "01" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "02" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "03" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "04" -> CardProto.CardStatus.CARD_STATUS_TERMINATED
            else -> CardProto.CardStatus.CARD_STATUS_UNKNOWN
        }
    }
}
