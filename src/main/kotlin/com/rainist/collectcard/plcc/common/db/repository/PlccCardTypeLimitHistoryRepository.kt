package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardTypeLimitHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardTypeLimitHistoryRepository : JpaRepository<PlccCardTypeLimitHistoryEntity, Long>
