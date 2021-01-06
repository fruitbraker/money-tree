package moneytree.api

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import java.util.UUID
import moneytree.domain.ExpenseCategory
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.http4k.buildRoutes
import moneytree.libs.test.commons.randomBigDecimal
import moneytree.libs.test.commons.randomString
import moneytree.persist.ExpenseCategoryRepository
import moneytree.validator.ExpenseCategoryValidator
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseCategoryApiTest {

    private val client = OkHttp()

    private val randomUUID = UUID.randomUUID()
    private val randomString = randomString()
    private val randomBigDecimal = randomBigDecimal()

    private val expenseCategory = ExpenseCategory(
        id = randomUUID,
        name = randomString,
        targetAmount = randomBigDecimal
    )

    private val expenseCategoryRepository = mockkClass(ExpenseCategoryRepository::class)
    private val expenseCategoryValidator = ExpenseCategoryValidator()
    private val expenseCategoryApi = ExpenseCategoryApi(expenseCategoryRepository, expenseCategoryValidator)

    private val server = buildRoutes(
        listOf(
            expenseCategoryApi.makeRoutes()
        )
    ).asServer(Jetty(0))

    private val url = setup()

    private fun setup(): String {
        every { expenseCategoryRepository.get() } returns listOf(expenseCategory).toOk()
        every { expenseCategoryRepository.getById(randomUUID) } returns expenseCategory.toOk()
        every { expenseCategoryRepository.insert(expenseCategory) } returns expenseCategory.toOk()
        every { expenseCategoryRepository.updateById(expenseCategory) } returns expenseCategory.toOk()

        server.start()
        return "http://localhost:${server.port()}/category/expense"
    }

    @Test
    fun `get happy path`() {
        val request = Request(
            Method.GET,
            url
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(expenseCategory).toJson()
    }

    @Test
    fun `getById happy path`() {
        val request = Request(
            Method.GET,
            "$url/$randomUUID"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe expenseCategory.toJson()
    }

    @Test
    fun `insert happy path`() {
        val request = Request(
            Method.POST,
            url
        ).with(expenseCategoryApi.lens of expenseCategory)

        val result = client(request)

        result.status shouldBe Status.CREATED
        result.bodyString() shouldBe expenseCategory.toJson()
    }

    @Test
    fun `updateById happy path`() {
        val request = Request(
            Method.PUT,
            "$url/$randomUUID"
        ).with(expenseCategoryApi.lens of expenseCategory)

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe expenseCategory.toJson()
    }
}
