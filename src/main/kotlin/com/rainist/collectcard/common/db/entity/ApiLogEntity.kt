package com.rainist.collectcard.common.db.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "api_access_log")
data class ApiLogEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var apiAccessLogId: Long? = null,

    var requestId: String? = null,

    var organizationId: String? = null,

    var banksaladUserId: Long? = null,

    var apiId: String? = null,

    var organizationApiId: String? = null,

    var requestUrl: String? = null,

    var httpMethod: String? = null,

    @Column(columnDefinition = "json")
    var requestHeaderJson: String? = null,

    @Column(columnDefinition = "json")
    var requestBodyJson: String? = null,

    var resultCode: String? = null,

    var responseCode: String? = null,

    @Column(columnDefinition = "json")
    var responseHeaderJson: String? = null,

    @Column(columnDefinition = "json")
    var responseBodyJson: String? = null,

    var requestDatetime: LocalDateTime? = null,

    var responseDatetime: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
