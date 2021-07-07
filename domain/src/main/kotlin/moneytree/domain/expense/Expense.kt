package moneytree.domain.expense

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.Filter
import moneytree.domain.entity.ExpenseCategory
import moneytree.domain.entity.Vendor
import moneytree.libs.commons.serde.TrimStringDeserializer

data class Expense(
    val expenseID: UUID?,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
    val vendorId: UUID,
    val expenseCategoryId: UUID,
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val notes: String,
    val hide: Boolean
)

data class ExpenseSummary(
    val id: UUID,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
    val vendor: Vendor,
    val expenseCategory: ExpenseCategory,
    val notes: String,
    val hide: Boolean
)

data class ExpenseSummaryFilter(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val vendorIds: List<UUID>,
    val expenseCategoryIds: List<UUID>
) : Filter()
