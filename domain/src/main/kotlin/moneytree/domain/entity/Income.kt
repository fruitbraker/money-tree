package moneytree.domain.entity

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class Income(
    val id: UUID?,
    val source: String,
    val incomeCategory: UUID,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
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
