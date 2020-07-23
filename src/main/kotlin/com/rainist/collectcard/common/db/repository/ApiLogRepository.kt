package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.ApiLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ApiLogRepository : JpaRepository<ApiLogEntity, Long> {
    fun findByRequestId(requestId: String): ApiLogEntity?
}
