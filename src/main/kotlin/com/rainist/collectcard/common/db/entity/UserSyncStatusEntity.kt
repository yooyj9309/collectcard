package com.rainist.collectcard.common.db.entity

import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
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
@Table(name = "user_sync_status")
data class UserSyncStatusEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userSyncStatusId: Long? = null,

    var banksaladUserId: Long? = null,

    var organizationId: String? = null,

    var transactionId: String? = null,

    var lastCheckAt: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime = DateTimeUtil.utcNowLocalDateTime(),

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
