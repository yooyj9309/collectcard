package com.rainist.collectcard.common.db.entity

import com.rainist.common.util.DateTimeUtil
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
@Table(name = "user_sync_status")
data class UserSyncStatusEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userSyncStatusId: Long? = null,

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var organizationId: String? = null,

    @Column(nullable = false)
    var transactionId: String? = null,

    @Column(nullable = false)
    var lastCheckAt: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime = DateTimeUtil.utcNowLocalDateTime(),

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
