package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardThresholdHistoryRepository : JpaRepository<PlccCardThresholdHistoryEntity, Long>
