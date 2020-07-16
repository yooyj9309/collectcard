package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardHistoryRepository : JpaRepository<CardHistoryEntity, Long>
