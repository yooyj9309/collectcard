package com.rainist.collectcard.config

import java.math.BigDecimal

class MapStructConfig {

    open class BigDecimalConverter {
        fun setScale(amount: BigDecimal?): BigDecimal? {
            return amount?.setScale(4)
        }
    }
}
