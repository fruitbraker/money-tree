package moneytree.domain.expense

import moneytree.domain.metadata.Metadata
import java.math.BigDecimal
import java.time.LocalDate

data class Expense(
    val id: Int?,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
    val vendor: Int,
    val category: String,
    val metadata: Metadata,
    val hide: Boolean
)
