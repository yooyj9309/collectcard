package com.rainist.collectcard.common.service

interface OrganizationService {

    fun getOrganizationByOrganizationId(organizationId: String): CardOrganization
    fun getOrganizationByObjectId(objectId: String): CardOrganization
}
