package moneytree.domain.expense

import java.util.UUID
import moneytree.libs.commons.result.Result

interface IExpenseRepository {
    fun get(filter: ExpenseSummaryFilter): Result<List<ExpenseSummary>, Throwable>
    fun getById(uuid: UUID): Result<ExpenseSummary?, Throwable>

    fun insert(newExpense: Expense): Result<Expense, Throwable>
    fun upsertById(expense: Expense, uuid: UUID): Result<Unit, Throwable>

    fun deleteById(uuid: UUID): Result<Unit, Throwable>
}
