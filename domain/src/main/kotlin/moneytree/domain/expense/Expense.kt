package moneytree.domain.expense

import moneytree.domain.metadata.Metadata
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class Expense(
    val id: UUID?,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
    val vendor: UUID,
    val category: UUID,
    val metadata: Metadata,
    val hide: Boolean
)
