package moneytree.persist.expense

import java.util.UUID
import moneytree.domain.Repository
import moneytree.domain.expense.Expense
import moneytree.persist.db.generated.Tables.EXPENSE
import org.jooq.DSLContext
import org.jooq.Record

class ExpenseRepository(
    private val dslContext: DSLContext
) : Repository<Expense> {

    private fun Record.toDomain(): Expense {
        return Expense(
            id = this[EXPENSE.ID],
            transactionDate = this[EXPENSE.TRANSACTION_DATE],
            transactionAmount = this[EXPENSE.TRANSACTION_AMOUNT],
            vendor = this[EXPENSE.VENDOR],
            expenseCategory = this[EXPENSE.EXPENSE_CATEGORY],
            notes = this[EXPENSE.NOTES],
            hide = this[EXPENSE.HIDE]
        )
    }

    override fun get(): List<Expense> {
        val result = dslContext.configuration().dsl()
            .select()
            .from(EXPENSE)
            .limit(100)
            .fetch()

        return result.mapNotNull { it.toDomain() }
    }

    override fun get(uuid: UUID): Expense {
        TODO("Not yet implemented")
    }
}
