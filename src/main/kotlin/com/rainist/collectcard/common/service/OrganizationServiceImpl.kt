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

        organizationsByOrganizationId[shinhancardOrganizationId] = shinhancardOrganization
        organizationsByOrganizationObjectId[shinhancardObjectId] = shinhancardOrganization
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
