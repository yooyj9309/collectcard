package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardHistoryRepository : JpaRepository<PlccCardHistoryEntity, Long>
