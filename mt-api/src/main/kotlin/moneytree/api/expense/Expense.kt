package moneytree.api.expense

import moneytree.api.metadata.Metadata
import java.math.BigDecimal
import java.time.LocalDate
import moneytree.domain.expense.Expense as ExpenseDomain
import moneytree.domain.metadata.Metadata as MetadataDomain

data class Expense(
    val id: Int?,
    val transactionDate: LocalDate,
    val transactionAmount: BigDecimal,
    val vendor: Int,
    val category: String,
    val metadata: Metadata,
    val hide: Boolean
) {
    companion object {
        fun toDomain(expense: Expense): ExpenseDomain {
            return ExpenseDomain(
                id = expense.id,
                transactionDate = expense.transactionDate,
                transactionAmount = expense.transactionAmount,
                vendor = expense.vendor,
                category = expense.category,
                metadata = MetadataDomain(
                    dateCreated = expense.metadata.dateCreated,
                    dateModified = expense.metadata.dateModified,
                    notes = expense.metadata.notes
                ),
                hide = expense.hide
            )
        }

        fun fromDomain(expenseDomain: ExpenseDomain): Expense {
            return Expense(
                id = expenseDomain.id,
                transactionDate = expenseDomain.transactionDate,
                transactionAmount = expenseDomain.transactionAmount,
                vendor = expenseDomain.vendor,
                category = expenseDomain.category,
                metadata = Metadata(
                    dateCreated = expenseDomain.metadata.dateCreated,
                    dateModified = expenseDomain.metadata.dateModified,
                    notes = expenseDomain.metadata.notes
                ),
                hide = expenseDomain.hide
            )
        }
    }
}
