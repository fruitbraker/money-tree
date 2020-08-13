package moneytree.persist.expense

import com.google.inject.Inject
import moneytree.domain.Result
import moneytree.domain.Try
import moneytree.domain.expense.Expense
import moneytree.domain.expense.IExpenseRepository
import moneytree.domain.metadata.Metadata
import moneytree.libs.commons.serde.jackson
import moneytree.persist.db.generated.tables.Expense.EXPENSE
import org.jooq.DSLContext
import org.jooq.Record

class ExpenseRepository @Inject constructor(
    private val dslContext: DSLContext
) : IExpenseRepository {

    private fun Record.toDomain(): Expense {
        return Expense(
            id = this[EXPENSE.ID],
            transactionDate = this[EXPENSE.TRANSACTION_DATE],
            transactionAmount = this[EXPENSE.TRANSACTION_AMOUNT],
            vendor = this[EXPENSE.VENDOR],
            category = this[EXPENSE.CATEGORY],
            metadata = jackson.readValue(this[EXPENSE.METADATA].data(), Metadata::class.java),
            hide = this[EXPENSE.HIDE]
        )
    }

    override fun get(): Result<List<Expense>, Throwable> {
        val result = dslContext.configuration().dsl()
            .select()
            .from(EXPENSE)
            .limit(100)
            .fetch()

        return Try {
            result.mapNotNull { it.toDomain() }
        }
    }

    override fun get(id: Int): Result<Expense, Throwable> {
        TODO("Not yet implemented")
    }

    override fun insert(expense: Expense): Result<Expense, Throwable> {
        TODO("Not yet implemented")
    }
}
