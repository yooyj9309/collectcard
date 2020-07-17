package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardLoanHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardLoanHistoryRepository : JpaRepository<CardLoanHistoryEntity, Long>
