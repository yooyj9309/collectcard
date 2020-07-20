package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardPaymentScheduledEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CardPaymentScheduledRepository : JpaRepository<CardPaymentScheduledEntity, Long>
