package moneytree.domain.expenseCategory

import moneytree.domain.metadata.Metadata
import java.math.BigDecimal
import java.util.*

data class ExpenseCategory(
    val id: UUID?,
    val name: String,
    val targetAmount: BigDecimal,
    val metadata: Metadata
)