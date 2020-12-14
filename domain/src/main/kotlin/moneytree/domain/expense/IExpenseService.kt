package moneytree.domain.expense

import moneytree.domain.Result

interface IExpenseService {
    fun get(): Result<List<Expense>, Throwable>
    fun get(id: Int): Result<Expense, Throwable>
    fun insert(expense: Expense): Result<Expense, Throwable>
    fun update(expense: Expense): Result<Int, Throwable>
    fun delete(id: Int): Result<Int, Throwable>
}
