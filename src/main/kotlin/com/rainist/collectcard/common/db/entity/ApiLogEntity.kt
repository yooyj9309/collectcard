package com.rainist.collectcard.common.db.entity

import com.rainist.collectcard.common.converter.ApiLogEncryptConverter
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Convert
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
    var executionRequestId: String? = null,

    @Column(nullable = false)
    var apiRequestId: String? = null,

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

    @Convert(converter = ApiLogEncryptConverter::class)
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT", name = "request_header_encrypted")
    var requestHeader: String? = null,

    @Convert(converter = ApiLogEncryptConverter::class)
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT", name = "request_body_encrypted")
    var requestBody: String? = null,

    @Convert(converter = ApiLogEncryptConverter::class)
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT", name = "transformed_request_header_encrypted")
    var transformedRequestHeader: String? = null,

    @Convert(converter = ApiLogEncryptConverter::class)
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT", name = "transformed_request_body_encrypted")
    var transformedRequestBody: String? = null,

    var resultCode: String? = null,

    var resultMessage: String? = null,

    var responseCode: String? = null,

    @Convert(converter = ApiLogEncryptConverter::class)
    @Column(columnDefinition = "MEDIUMTEXT", name = "response_header_encrypted")
    var responseHeader: String? = null,

    @Convert(converter = ApiLogEncryptConverter::class)
    @Column(columnDefinition = "MEDIUMTEXT", name = "response_body_encrypted")
    var responseBody: String? = null,

    @Convert(converter = ApiLogEncryptConverter::class)
    @Column(columnDefinition = "MEDIUMTEXT", name = "transformed_response_header_encrypted")
    var transformedResponseHeader: String? = null,

    @Convert(converter = ApiLogEncryptConverter::class)
    @Column(columnDefinition = "MEDIUMTEXT", name = "transformed_response_body_encrypted")
    var transformedResponseBody: String? = null,

    @Column(nullable = false)
    var requestDatetime: LocalDateTime? = null,

    var responseDatetime: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
