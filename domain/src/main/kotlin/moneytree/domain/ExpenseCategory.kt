package moneytree.domain

import java.math.BigDecimal
import java.util.UUID

data class ExpenseCategory(
    val id: UUID?,
    val name: String,
    val targetAmount: BigDecimal
)
