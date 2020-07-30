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
@Table(name = "api_log")
data class ApiLogEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var apiLogId: Long? = null,

    @Column(nullable = false)
    var requestId: String? = null,

    @Column(nullable = false)
    var organizationId: String? = null,

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var apiId: String? = null,

    @Column(nullable = false)
    var organizationApiId: String? = null,

    @Column(nullable = false)
    var requestUrl: String? = null,

    @Column(nullable = false)
    var httpMethod: String? = null,

    @Column(nullable = false, columnDefinition = "json")
    var requestHeaderJson: String? = null,

    @Column(nullable = false, columnDefinition = "json")
    var requestBodyJson: String? = null,

    @Column(nullable = false, columnDefinition = "json")
    var transformedRequestHeaderJson: String? = null,

    @Column(nullable = false, columnDefinition = "json")
    var transformedRequestBodyJson: String? = null,

    var resultCode: String? = null,

    var responseCode: String? = null,

    @Column(columnDefinition = "json")
    var responseHeaderJson: String? = null,

    @Column(columnDefinition = "json")
    var responseBodyJson: String? = null,

    @Column(columnDefinition = "json")
    var transformedResponseHeaderJson: String? = null,

    @Column(columnDefinition = "json")
    var transformedResponseBodyJson: String? = null,

    @Column(nullable = false)
    var requestDatetime: LocalDateTime? = null,

    var responseDatetime: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
