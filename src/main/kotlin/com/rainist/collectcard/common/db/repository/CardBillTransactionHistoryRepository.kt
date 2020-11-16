package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardBillTransactionHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardBillTransactionHistoryRepository : JpaRepository<CardBillTransactionHistoryEntity, Long>
