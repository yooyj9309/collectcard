package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CreditLimitHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CreditLimitHistoryRepository : JpaRepository<CreditLimitHistoryEntity, Long>
