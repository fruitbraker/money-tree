package moneytree.domain.expense

import moneytree.domain.metadata.Metadata
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class ExpenseSummary(
    val id: UUID?,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
    val vendorName: String,
    val expenseCategory: String,
    val metadata: Metadata,
    val hide: Boolean
)
