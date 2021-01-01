package moneytree.domain.expense

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class Expense(
    val id: UUID?,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
    val vendor: UUID,
    val expenseCategory: UUID,
    val notes: String,
    val hide: Boolean
)

data class ExpenseSummary(
    val id: UUID,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
    val vendorName: String,
    val expenseCategory: String,
    val notes: String,
    val hide: Boolean
)
