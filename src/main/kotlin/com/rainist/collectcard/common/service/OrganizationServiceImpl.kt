package com.rainist.collectcard.common.service

import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.common.log.Log
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class OrganizationServiceImpl : OrganizationService {

    @Value("\${shinhancard.clientId}")
    private lateinit var shinhancardClientId: String

    @Value("\${shinhancard.organizationId}")
    private lateinit var shinhancardOrganizationId: String

    @Value("\${shinhancard.objectId}")
    private lateinit var shinhancardObjectId: String

    @Value("\${lottecard.organizationId}")
    private lateinit var lottecardOrganizationId: String

    // TODO 롯데카드 organization objectid 추가하기
    @Value("\${lottecard.objectId}")
    private lateinit var lottecardObjectId: String

    private val organizationsByOrganizationId = LinkedHashMap<String, CardOrganization>()
    private val organizationsByOrganizationObjectId = LinkedHashMap<String, CardOrganization>()

    companion object : Log

    @PostConstruct
    fun init() {
        val shinhancardOrganization = CardOrganization().apply {
            this.name = shinhancardOrganizationId
            this.code = "SHC"
            this.clientId = shinhancardClientId
            this.organizationId = shinhancardOrganizationId
            this.organizationObjectId = shinhancardObjectId
            this.maxMonth = 6
            this.division = 2
        }

        val lottecardOrganization = CardOrganization().apply {
            this.name = lottecardOrganizationId
            this.code = "LTC"
            this.clientId = ""
            this.organizationId = lottecardOrganizationId
            this.organizationObjectId = lottecardObjectId
        }

        organizationsByOrganizationId[shinhancardOrganizationId] = shinhancardOrganization
        organizationsByOrganizationObjectId[shinhancardObjectId] = shinhancardOrganization

        organizationsByOrganizationId[lottecardOrganizationId] = lottecardOrganization
        organizationsByOrganizationObjectId[lottecardObjectId] = lottecardOrganization
    }

    override fun getOrganizationByOrganizationId(organizationId: String): CardOrganization {
        return organizationsByOrganizationId[organizationId]
            ?: throw CollectcardException("Fail to resolve organization. organizationId: $organizationId")
    }

    override fun getOrganizationByObjectId(objectId: String): CardOrganization {
        return organizationsByOrganizationObjectId[objectId]
            ?: throw CollectcardException("Fail to resolve organization. organizationObjectId: $objectId")
    }
}
