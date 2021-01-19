package moneytree.api

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.Income
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.http4k.buildRoutes
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.IncomeRepository
import moneytree.validator.IncomeValidator
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
class IncomeApiTest {
    private val client = OkHttp()

    private val randomUUID = UUID.randomUUID()
    private val randomSource = randomString()
    private val todayLocalDate = LocalDate.now()
    private val incomeCategoryId = UUID.randomUUID()
    private val randomTransactionAmount = randomBigDecimal()
    private val notes = randomString()
    private val hide = false

    private val income = Income(
        id = randomUUID,
        source = randomSource,
        incomeCategory = incomeCategoryId,
        transactionDate = todayLocalDate,
        transactionAmount = randomTransactionAmount,
        notes = notes,
        hide = hide
    )

    private val incomeRepository = mockkClass(IncomeRepository::class)
    private val incomeValidator = IncomeValidator()
    private val incomeApi = IncomeApi(incomeRepository, incomeValidator)

    private val server = buildRoutes(
        listOf(
            incomeApi.makeRoutes()
        )
    ).asServer(Jetty(0))

    private val url = setup()

    private fun setup(): String {
        every { incomeRepository.get() } returns listOf(income).toOk()
        every { incomeRepository.getById(randomUUID) } returns income.toOk()
        every { incomeRepository.insert(income) } returns income.toOk()
        every { incomeRepository.upsertById(income, randomUUID) } returns income.toOk()

        server.start()
        return "http://localhost:${server.port()}/income"
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
        result.bodyString() shouldBe listOf(income).toJson()
    }

    @Test
    fun `getById happy path`() {
        val request = Request(
            Method.GET,
            "$url/$randomUUID"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe income.toJson()
    }

    @Test
    fun `insert happy path`() {
        val request = Request(
            Method.POST,
            url
        ).with(incomeApi.lens of income)

        val result = client(request)

        result.status shouldBe Status.CREATED
        result.bodyString() shouldBe income.toJson()
    }

    @Test
    fun `updateById happy path`() {
        val request = Request(
            Method.PUT,
            "$url/$randomUUID"
        ).with(incomeApi.lens of income)

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe income.toJson()
    }
}
