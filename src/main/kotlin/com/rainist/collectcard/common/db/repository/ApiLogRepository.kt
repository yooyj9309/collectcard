package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.ApiLogEntity
import java.time.LocalDateTime
import org.springframework.data.jpa.repository.JpaRepository

interface ApiLogRepository : JpaRepository<ApiLogEntity, Long> {
    fun findByExecutionRequestIdAndApiRequestIdAndCreatedAtBetween(
        executionRequestId: String,
        apiRequestId: String,
        createAtStart: LocalDateTime,
        createAtEnd: LocalDateTime
    ): ApiLogEntity?

    fun findByExecutionRequestIdAndCreatedAtBetween(
        executionRequestId: String,
        createAtStart: LocalDateTime,
        createAtEnd: LocalDateTime
    ): List<ApiLogEntity>
}
