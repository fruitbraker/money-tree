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
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
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
    private val expenseCategoryApi = ExpenseCategoryApi(expenseCategoryRepository)

    private val server = buildRoutes(
        listOf(
            expenseCategoryApi.makeRoutes()
        )
    ).asServer(Jetty(9000))

    @BeforeAll
    fun setup() {
        every { expenseCategoryRepository.get() } returns listOf(expenseCategory).toOk()
        every { expenseCategoryRepository.getById(randomUUID) } returns expenseCategory.toOk()

        server.start()
    }

    @AfterAll
    fun teardown() {
        server.stop()
    }

    companion object {
        const val GET_URL = "http://localhost:9000/expense-category"
    }

    @Test
    fun `get happy path`() {
        val request = Request(
            Method.GET,
            GET_URL
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(expenseCategory).toJson()
    }

    @Test
    fun `getById happy path`() {
        val request = Request(
            Method.GET,
            "$GET_URL/$randomUUID"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe expenseCategory.toJson()
    }
}
