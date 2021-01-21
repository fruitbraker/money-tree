package moneytree.api

import io.mockk.every
import io.mockk.mockkClass
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.Expense
import moneytree.domain.entity.ExpenseSummary
import moneytree.libs.commons.result.toOk
import moneytree.libs.http4k.buildRoutes
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.ExpenseRepository
import moneytree.validator.ExpenseValidator
import org.http4k.server.Jetty
import org.http4k.server.asServer

class ExpenseApiTest : RoutesWithSummaryTest<Expense, ExpenseSummary>() {

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
    override val entityValidator = ExpenseValidator()
    override val entityApi = ExpenseApi(entityRepository as SummaryRepository<ExpenseSummary>, entityRepository, entityValidator)

    override val server = buildRoutes(
        listOf(
            entityApi.makeRoutes()
        )
    ).asServer(Jetty(0))

    override val url = setup()

    override fun setup(): String {
        every { entityRepository.get() } returns listOf(entity).toOk()
        every { entityRepository.getById(any()) } returns entity.toOk()
        every { entityRepository.insert(entity) } returns entity.toOk()
        every { entityRepository.upsertById(entity, any()) } returns entity.toOk()
        every { entityRepository.getSummary() } returns listOf(entitySummary).toOk()
        every { entityRepository.getSummaryById(any()) } returns entitySummary.toOk()

        server.start()
        return "http://localhost:${server.port()}/expense"
    }
}
