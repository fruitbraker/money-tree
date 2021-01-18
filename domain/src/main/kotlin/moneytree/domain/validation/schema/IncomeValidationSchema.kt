package moneytree.domain.validation.schema

import java.math.BigDecimal

class IncomeValidationSchema {
    companion object {
        val INCOME_AMOUNT_MIN = BigDecimal(0.00)
        val INCOME_AMOUNT_MAX = BigDecimal(99999999.9999)
        const val INCOME_NOTES_MAX_LENGTH = 256
        const val INCOME_SOURCE_MAX_LENGTH = 256
    }
}
