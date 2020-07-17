package com.rainist.collectcard.cardloans.repository

import com.rainist.collectcard.cardloans.entity.CardLoanHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardLoanHistoryRepository : JpaRepository<CardLoanHistoryEntity, Long>
