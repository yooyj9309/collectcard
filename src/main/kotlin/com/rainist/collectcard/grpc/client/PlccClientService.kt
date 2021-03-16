package com.rainist.collectcard.grpc.client

import com.github.banksalad.idl.apis.v1.card.CardProto
import com.github.banksalad.idl.apis.v1.plcc.PlccGrpc
import com.github.banksalad.idl.apis.v1.plcc.PlccProto
import com.google.protobuf.StringValue
import com.rainist.collectcard.plcc.dto.PlccCardDto
import com.rainist.collectcard.plcc.dto.SyncType
import com.rainist.common.util.DateTimeUtil
import java.time.ZoneId
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
        userId: String?,
        data: List<PlccCardDto>,
        syncType: SyncType
    ): PlccProto.SyncPlccsByCollectcardDataResponse? {
        val requestBuilder = PlccProto.SyncPlccsByCollectcardDataRequest.newBuilder()
            .setOrganizationId(organizationId)
            .setCi(ci)
            .addAllData(
                data.map {
                    PlccProto.CollectcardPlccData.newBuilder()
                        .setName(it.cardName)
                        .setNumber(it.cardNumberMask)
                        .setExternalId(it.cid)
                        .setProductName(StringValue.of(it.cardProductName))
                        .setInternationalBrand(parseInternalBrand(it.internationalBrandName))
                        .setOwnerName(StringValue.of(it.cardOwnerName))
                        .setCardOwnerType(parseCardOwnerType(it.ownerType))
//                        .setExternalState(StringValue.of(parseExternalState()))
                        .setStatus(cardIssueStatusToProtoEnum(it.cardIssueStatus))
                        .setIssuedAtMs(DateTimeUtil.stringDateToEpochMilliSecond(it.issuedDay
                            ?: "", "yyyyMMdd", ZoneId.systemDefault(), ZoneOffset.UTC))
                        .setExpiresAtMs(DateTimeUtil.stringDateToEpochMilliSecond(it.expiresYearMonth + "01"
                            ?: "", "yyyyMMdd", ZoneId.systemDefault(), ZoneOffset.UTC))
                        .setAgreedAtMs(DateTimeUtil.stringDateToEpochMilliSecond(it.cardApplicationDay
                            ?: "", "yyyyMMdd", ZoneId.systemDefault(), ZoneOffset.UTC))
                        .build()
                }
            )
            .setSyncType(syncTypetoProtoEnum(syncType))

        if (userId != null) {
            requestBuilder.setUserId(StringValue.of(userId))
        }

        return plccBlockingStub.syncPlccsByCollectcardData(requestBuilder.build())
    }

    private fun syncTypetoProtoEnum(syncType: SyncType?): PlccProto.SyncUserPlccType {
        return when (syncType) {
            SyncType.ISSUED -> PlccProto.SyncUserPlccType.SYNC_PLCC_TYPE_ISSUED
            SyncType.STATUS_UPDATED -> PlccProto.SyncUserPlccType.SYNC_PLCC_TYPE_STATUS_UPDATED
            else -> PlccProto.SyncUserPlccType.SYNC_PLCC_TYPE_UNKNOWN
        }
    }

    private fun cardIssueStatusToProtoEnum(statusCode: String?): CardProto.CardStatus {
        return when (statusCode) {
            "00" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "01" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "02" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "03" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "04" -> CardProto.CardStatus.CARD_STATUS_TERMINATED
            else -> CardProto.CardStatus.CARD_STATUS_UNKNOWN
        }
    }

    private fun parseCardOwnerType(ownerType: String?): CardProto.CardOwnerType {
        return when (ownerType) {
            "본인" -> CardProto.CardOwnerType.CARD_OWNER_TYPE_OWNER
            else -> CardProto.CardOwnerType.CARD_OWNER_TYPE_FAMILY
        }
    }

    private fun parseInternalBrand(internalBrandName: String?): CardProto.CardInternationalBrand {
        return when (internalBrandName?.toUpperCase()) {
            "VISA" -> CardProto.CardInternationalBrand.CARD_INTERNATIONAL_BRAND_VISA
            "AMEX" -> CardProto.CardInternationalBrand.CARD_INTERNATIONAL_BRAND_AMEX
            "MASTER" -> CardProto.CardInternationalBrand.CARD_INTERNATIONAL_BRAND_MASTERCARD
            "MASTERCARD" -> CardProto.CardInternationalBrand.CARD_INTERNATIONAL_BRAND_MASTERCARD
            else -> CardProto.CardInternationalBrand.CARD_INTERNATIONAL_BRAND_UNKNOWN
        }
    }
}
