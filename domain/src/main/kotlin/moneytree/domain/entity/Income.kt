package moneytree.domain.entity

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.Filter
import moneytree.libs.commons.serde.TrimStringDeserializer

data class Income(
    val incomeId: UUID?,
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val source: String,
    val incomeCategoryId: UUID,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val notes: String,
    val hide: Boolean
)

data class IncomeSummary(
    val id: UUID,
    val source: String,
    val incomeCategoryId: UUID,
    val incomeCategoryName: String,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
    val notes: String,
    val hide: Boolean
)

data class IncomeSummaryFilter(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val incomeCategoryIds: List<UUID>
) : Filter()
