package moneytree.api

import io.mockk.mockkClass
import java.time.LocalDate
import java.util.UUID
import moneytree.api.contracts.RoutesWithSummaryTest
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.Expense
import moneytree.domain.entity.ExpenseSummary
import moneytree.domain.entity.ExpenseSummaryFilter
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.ExpenseRepository
import moneytree.validator.ExpenseValidator

class ExpenseApiTest : RoutesWithSummaryTest<Expense, ExpenseSummary, ExpenseSummaryFilter>() {

    private val randomUUID = UUID.randomUUID()
    private val todayLocalDate = LocalDate.now()
    private val randomTransactionAmount = randomBigDecimal()
    private val vendorId = UUID.randomUUID()
    private val vendorName = randomString()
    private val expenseCategoryId = UUID.randomUUID()
    private val expenseCategory = randomString()
    private val notes = randomString()
    private val hide = false

    override val entity = Expense(
        id = randomUUID,
        transactionAmount = randomTransactionAmount,
        transactionDate = todayLocalDate,
        vendor = vendorId,
        expenseCategory = expenseCategoryId,
        notes = notes,
        hide = hide
    )

    override val entitySummary = ExpenseSummary(
        id = randomUUID,
        transactionAmount = randomTransactionAmount,
        transactionDate = todayLocalDate,
        vendorId = vendorId,
        vendorName = vendorName,
        expenseCategoryId = expenseCategoryId,
        expenseCategoryName = expenseCategory,
        notes = notes,
        hide = hide
    )

    override val entityRepository = mockkClass(ExpenseRepository::class)
    override val entitySummaryRepository = entityRepository as SummaryRepository<ExpenseSummary, ExpenseSummaryFilter>
    override val entityValidator = ExpenseValidator()
    override val entityApi = ExpenseApi(entityRepository as SummaryRepository<ExpenseSummary, ExpenseSummaryFilter>, entityRepository, entityValidator)

    override val entityPath: String = "/expense"
    override val entitySummaryPath: String = "/expense/summary"

    override val filter: ExpenseSummaryFilter = ExpenseSummaryFilter(
        startDate = LocalDate.now().minusDays(1),
        endDate = LocalDate.now(),
        vendorIds = listOf(entitySummary.vendorId),
        expenseCategoryIds = listOf(entitySummary.expenseCategoryId)
    )
}
