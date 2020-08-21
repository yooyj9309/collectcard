package com.rainist.collectcard.common.collect.execution.shinhancard

import com.rainist.collect.common.api.Pagination
import com.rainist.collect.common.execution.Execution
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.card.dto.ListCardsResponseDataBody
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.exception.CollectExecutionExceptionHandler
import java.util.function.BinaryOperator

class ShinhancardCardExecution {
    companion object {

        val mergeCards =
            BinaryOperator { prev: ListCardsResponse, next: ListCardsResponse ->
                prev.resultCodes.add(next.dataHeader?.resultCode ?: ResultCode.UNKNOWN)

                val prevCards = prev.dataBody?.cards ?: mutableListOf()
                val nextCards = next.dataBody?.cards ?: mutableListOf()

                prevCards.addAll(nextCards)

                prev.dataBody = ListCardsResponseDataBody(cards = prevCards, nextKey = next.dataBody?.nextKey)
                prev
            }

        // 유효카드 정보조회 SHC_HPG00548
        val cardShinhancardCards =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_cards)
                .to(ListCardsResponse::class.java)
                .exceptionally { throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        Organization.shinhancard.name,
                        "cardShinhancardCards",
                        ShinhancardApis.card_shinhancard_cards.id,
                        throwable
                    )
                }
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(mergeCards)
                        .build()
                ).build()
    }
}
