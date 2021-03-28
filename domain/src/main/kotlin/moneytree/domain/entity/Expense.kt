package moneytree.domain.entity

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
    val vendorId: UUID,
    val vendorName: String,
    val expenseCategoryId: UUID,
    val expenseCategoryName: String,
    val notes: String,
    val hide: Boolean
)

data class ExpenseSummaryFilter(
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val vendorIds: List<UUID>?,
    val expenseCategoryIds: List<UUID>?
) : Filter()
