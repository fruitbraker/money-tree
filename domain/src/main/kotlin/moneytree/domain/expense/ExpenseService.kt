package moneytree.domain.expense

import com.google.inject.Inject
import com.google.inject.Singleton
import moneytree.domain.Result
import javax.transaction.Transactional

@Singleton
@Transactional
class ExpenseService @Inject constructor(
    private val expenseRepository: IExpenseRepository
) : IExpenseService {

    override fun get(): Result<List<Expense>, Throwable> = expenseRepository.get()

    override fun get(id: Int): Result<Expense, Throwable> = expenseRepository.get(id)

    override fun insert(expense: Expense): Result<Expense, Throwable> = expenseRepository.insert(expense)

    override fun update(expense: Expense): Result<Int, Throwable> = expenseRepository.update(expense)

    override fun delete(id: Int): Result<Int, Throwable> = expenseRepository.delete(id)
}
