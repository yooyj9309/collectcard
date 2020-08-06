package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardBillHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardBillHistoryRepository : JpaRepository<CardBillHistoryEntity, Long>
