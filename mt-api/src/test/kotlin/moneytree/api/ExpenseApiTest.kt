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
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseApiTest {
    private val client = OkHttp()

    private val randomUUID = UUID.randomUUID()
    private val todayLocalDate = LocalDate.now()
    private val randomTransactionAmount = randomBigDecimal()
    private val vendorId = UUID.randomUUID()
    private val vendorName = randomString()
    private val expenseCategoryId = UUID.randomUUID()
    private val expenseCategory = randomString()
    private val notes = randomString()
    private val hide = false

    private val expense = Expense(
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

    private val expenseRepository = mockkClass(ExpenseRepository::class)
    private val expenseValidator = ExpenseValidator()
    private val expenseApi = ExpenseApi(expenseRepository, expenseValidator)

    private val server = buildRoutes(
        listOf(
            expenseApi.makeRoutes()
        )
    ).asServer(Jetty(0))

    private val url = setup()

    private fun setup(): String {
        every { expenseRepository.get() } returns listOf(expense).toOk()
        every { expenseRepository.getById(randomUUID) } returns expense.toOk()
        every { expenseRepository.insert(expense) } returns expense.toOk()
        every { expenseRepository.upsertById(expense, randomUUID) } returns expense.toOk()
        every { expenseRepository.getExpenseSummary() } returns listOf(expenseSummary).toOk()
        every { expenseRepository.getExpenseSummaryById(randomUUID) } returns expenseSummary.toOk()

        server.start()
        return "http://localhost:${server.port()}/expense"
    }

    @AfterAll
    fun cleanUp() {
        server.stop()
        client.close()
    }

    @Test
    fun `get happy path`() {
        val request = Request(
            Method.GET,
            url
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(expense).toJson()
    }

    @Test
    fun `getById happy path`() {
        val request = Request(
            Method.GET,
            "$url/$randomUUID"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe expense.toJson()
    }

    @Test
    fun `insert happy path`() {
        val request = Request(
            Method.POST,
            url
        ).with(expenseApi.lens of expense)

        val result = client(request)

        result.status shouldBe Status.CREATED
        result.bodyString() shouldBe expense.toJson()
    }

    @Test
    fun `updateById happy path`() {
        val request = Request(
            Method.PUT,
            "$url/$randomUUID"
        ).with(expenseApi.lens of expense)

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe expense.toJson()
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
