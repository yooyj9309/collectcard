package com.rainist.collectcard.common.service

import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.common.log.Log
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class OrganizationServiceImpl : OrganizationService {

    @Value("\${shinhancard.clientId}")
    lateinit var shinhancardClientId: String

    @Value("\${shinhancard.organizationId}")
    lateinit var shinhancardOrganizationId: String

    @Value("\${shinhancard.objectId}")
    lateinit var shinhancardObjectId: String

    private val organizationsByOrganizationId = LinkedHashMap<String, Organization>()
    private val organizationsByOrganizationObjectId = LinkedHashMap<String, Organization>()

    companion object : Log

    @PostConstruct
    fun init() {
        val shinhancardOrganization = Organization().apply {
            this.name = shinhancardOrganizationId
            this.code = "SHC"
            this.clientId = shinhancardClientId
            this.organizationId = shinhancardOrganizationId
            this.organizationObjectId = shinhancardObjectId
        }

        organizationsByOrganizationId[shinhancardOrganizationId] = shinhancardOrganization
        organizationsByOrganizationObjectId[shinhancardObjectId] = shinhancardOrganization
    }

    override fun getOrganizationByOrganizationId(organizationId: String): Organization {
        return organizationsByOrganizationId[organizationId]
            ?: throw CollectcardException("Fail to resolve organization. organizationId: $organizationId")
    }

    override fun getOrganizationByObjectId(objectId: String): Organization {
        return organizationsByOrganizationObjectId[objectId]
            ?: throw CollectcardException("Fail to resolve organization. organizationObjectId: $objectId")
    }
}
