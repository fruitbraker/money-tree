package moneytree.domain.entity

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.Filter
import moneytree.libs.commons.serde.TrimStringDeserializer

data class ExpenseCategory(
    val expenseCategoryId: UUID?,
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val name: String,
    val targetAmount: BigDecimal
)

data class ExpenseCategorySummary(
    val id: UUID,
    val name: String,
    val totalAmount: BigDecimal
)

data class ExpenseCategoryFilter(
    val ids: List<UUID>,
    val startDate: LocalDate,
    val endDate: LocalDate
) : Filter()
