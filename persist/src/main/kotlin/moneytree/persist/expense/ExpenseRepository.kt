package moneytree.persist.expense

import java.util.UUID
import moneytree.domain.Repository
import moneytree.domain.expense.Expense
import org.jooq.DSLContext

class ExpenseRepository(
    private val dslContext: DSLContext
) : Repository<Expense> {
    override fun get(): List<Expense> {
        TODO("Not yet implemented")
    }

    override fun get(uuid: UUID): Expense {
        TODO("Not yet implemented")
    }
}
