package com.rainist.collectcard.common.service

interface OrganizationService {

    fun getOrganizationByOrganizationId(organizationId: String): Organization
    fun getOrganizationByObjectId(objectId: String): Organization
}
