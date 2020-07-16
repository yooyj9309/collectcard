package com.rainist.collectcard.cardcreditlimit.repository

import com.rainist.collectcard.cardcreditlimit.entity.CreditLimitHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CreditLimitHistoryRepository : JpaRepository<CreditLimitHistoryEntity, Long>
