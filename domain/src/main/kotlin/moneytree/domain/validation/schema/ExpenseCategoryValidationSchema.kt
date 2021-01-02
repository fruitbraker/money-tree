package moneytree.domain.validation.schema

import java.math.BigDecimal

class ExpenseCategoryValidationSchema {
    companion object {
        const val NAME_LENGTH = 32
        val TARGET_AMOUNT_MIN = BigDecimal(0.00)
        val TARGET_AMOUNT_MAX = BigDecimal(99999999.9999)
    }
}
