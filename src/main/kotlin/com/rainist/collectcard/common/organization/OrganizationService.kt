package com.rainist.collectcard.common.organization

interface OrganizationService {

    fun getOrganizationByOrganizationId(organizationId: String): Organization
    fun getOrganizationByObjectId(objectId: String): Organization
}
