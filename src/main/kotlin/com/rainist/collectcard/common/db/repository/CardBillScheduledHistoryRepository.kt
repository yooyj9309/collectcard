package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardBillScheduledHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardBillScheduledHistoryRepository : JpaRepository<CardBillScheduledHistoryEntity, Long>
