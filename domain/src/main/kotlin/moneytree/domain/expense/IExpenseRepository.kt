package moneytree.domain.expense

import moneytree.domain.Result

interface IExpenseRepository {
    fun get(): Result<List<Expense>, Throwable>
    fun get(id: Int): Result<Expense, Throwable>
    fun insert(expense: Expense): Result<Expense, Throwable>
}