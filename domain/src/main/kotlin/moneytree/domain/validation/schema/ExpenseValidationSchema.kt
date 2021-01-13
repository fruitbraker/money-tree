package moneytree.domain.validation.schema

import java.math.BigDecimal

class ExpenseValidationSchema {
    companion object {
        val EXPENSE_AMOUNT_MIN = BigDecimal(0.00)
        val EXPENSE_AMOUNT_MAX = BigDecimal(99999999.9999)
        const val EXPENSE_NOTES_MAX_LENGTH = 256
    }
}
