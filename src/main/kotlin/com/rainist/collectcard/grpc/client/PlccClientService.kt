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
                data.map { cardDto ->
                    val cardBuilder = PlccProto.CollectcardPlccData.newBuilder()
                    cardDto.cardName?.let { cardBuilder.setName(it) }
                    cardDto.cardNumberMask?.let { cardBuilder.setNumber(it) }
                    cardDto.cid?.let { cardBuilder.setExternalId(it) }
                    cardDto.cardProductName?.let { cardBuilder.setProductName(StringValue.of(it)) }
                    cardDto.ownerType?.let { cardBuilder.setCardOwnerType(parseCardOwnerType(it)) }
                    cardDto.internationalBrandName?.let { cardBuilder.setInternationalBrand(parseInternalBrand(it)) }
                    cardDto.cardOwnerName?.let { cardBuilder.setOwnerName(StringValue.of(it)) }
                    cardDto.cardIssueStatus?.let {
                        cardBuilder.setExternalState(StringValue.of(it))
                        cardBuilder.setStatus(cardIssueStatusToProtoEnum(it))
                    }
                    cardDto.issuedDay?.let { cardBuilder.setIssuedAtMs(DateTimeUtil.stringDateToEpochMilliSecond(it, "yyyyMMdd", ZoneId.systemDefault(), ZoneOffset.UTC)) }
                    cardDto.expiresYearMonth?.let { cardBuilder.setExpiresAtMs(DateTimeUtil.stringDateToEpochMilliSecond(it + "01", "yyyyMMdd", ZoneId.systemDefault(), ZoneOffset.UTC)) }
                    cardDto.cardApplicationDay?.let { cardBuilder.setAgreedAtMs(DateTimeUtil.stringDateToEpochMilliSecond(it, "yyyyMMdd", ZoneId.systemDefault(), ZoneOffset.UTC)) }

                    cardBuilder.build()
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
        val lostCodes = mutableListOf("WB10", "WB20", "WD10", "WM10", "WQ10", "WY10", "WY20", "WY30", "WY40", "WY50")
        val terminatedCodes = mutableListOf("UC10", "UC20", "UC30", "UC40")
        val reissuedCodes = mutableListOf("UH10", "UH20", "UH30", "UH40", "UH50")
        val expiredCodes = mutableListOf("UE10", "UE20")
        val registeredCodes = mutableListOf("SU10", "SU20")

        return when (statusCode) {
            "00" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "01" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "02" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "03" -> CardProto.CardStatus.CARD_STATUS_REGISTERED
            "04" -> CardProto.CardStatus.CARD_STATUS_TERMINATED
            in lostCodes -> CardProto.CardStatus.CARD_STATUS_LOST
            in terminatedCodes -> CardProto.CardStatus.CARD_STATUS_TERMINATED
            in reissuedCodes -> CardProto.CardStatus.CARD_STATUS_SUSPENDED
            in expiredCodes -> CardProto.CardStatus.CARD_STATUS_TERMINATED
            in registeredCodes -> CardProto.CardStatus.CARD_STATUS_REGISTERED
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
