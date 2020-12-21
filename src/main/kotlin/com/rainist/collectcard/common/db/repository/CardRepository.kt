package com.rainist.collectcard.common.db.repository

import com.rainist.collectcard.common.db.entity.CardEntity
import java.time.LocalDateTime
import org.springframework.data.jpa.repository.JpaRepository

interface CardRepository : JpaRepository<CardEntity, Long> {

    fun findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(
        banksaladUserId: Long,
        cardCompanyId: String,
        cardCompanyCardId: String
    ): CardEntity?

    fun findAllByBanksaladUserIdAndCardCompanyIdAndLastCheckAt(
        banksaladUserId: Long,
        cardCompanyCardId: String,
        lastCheckAt: LocalDateTime?
    ): List<CardEntity>
}
