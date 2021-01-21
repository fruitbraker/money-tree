package moneytree.api

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.Expense
import moneytree.domain.entity.ExpenseSummary
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.http4k.buildRoutes
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.ExpenseRepository
import moneytree.validator.ExpenseValidator
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.Test

class ExpenseApiTest : BasicRoutesTest<Expense>() {

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

    private val expenseSummary = ExpenseSummary(
        id = randomUUID,
        transactionAmount = randomTransactionAmount,
        transactionDate = todayLocalDate,
        vendorId = vendorId,
        vendorName = vendorName,
        expenseCategoryId = expenseCategoryId,
        expenseCategory = expenseCategory,
        notes = notes,
        hide = hide
    )

    override val entityRepository = mockkClass(ExpenseRepository::class)
    override val entityValidator = ExpenseValidator()
    override val entityApi = ExpenseApi(entityRepository, entityValidator)

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
        every { entityRepository.getExpenseSummary() } returns listOf(expenseSummary).toOk()
        every { entityRepository.getExpenseSummaryById(any()) } returns expenseSummary.toOk()

        server.start()
        return "http://localhost:${server.port()}/expense"
    }

    @Test
    fun `getExpenseSummary happy path`() {
        val request = Request(
            Method.GET,
            "$url/summary"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(expenseSummary).toJson()
    }

    @Test
    fun `getExpenseSummaryById happy path`() {
        val request = Request(
            Method.GET,
            "$url/summary/$randomUUID"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe expenseSummary.toJson()
    }
}
