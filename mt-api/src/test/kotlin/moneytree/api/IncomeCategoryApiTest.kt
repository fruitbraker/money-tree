package moneytree.api

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import java.util.UUID
import moneytree.domain.entity.IncomeCategory
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.http4k.buildRoutes
import moneytree.libs.test.commons.randomString
import moneytree.persist.repository.IncomeCategoryRepository
import moneytree.validator.IncomeCategoryValidator
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
class IncomeCategoryApiTest {
    private val client = OkHttp()

    private val randomUUID = UUID.randomUUID()
    private val randomString = randomString()

    private val incomeCategory = IncomeCategory(
        id = randomUUID,
        name = randomString,
    )

    private val incomeCategoryRepository = mockkClass(IncomeCategoryRepository::class)
    private val incomeCategoryValidator = IncomeCategoryValidator()
    private val incomeCategoryApi = IncomeCategoryApi(incomeCategoryRepository, incomeCategoryValidator)

    private val server = buildRoutes(
        listOf(
            incomeCategoryApi.makeRoutes()
        )
    ).asServer(Jetty(0))

    private val url = setup()

    private fun setup(): String {
        every { incomeCategoryRepository.get() } returns listOf(incomeCategory).toOk()
        every { incomeCategoryRepository.getById(randomUUID) } returns incomeCategory.toOk()
        every { incomeCategoryRepository.insert(incomeCategory) } returns incomeCategory.toOk()
        every { incomeCategoryRepository.upsertById(incomeCategory, randomUUID) } returns incomeCategory.toOk()

        server.start()
        return "http://localhost:${server.port()}/category/income"
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
        result.bodyString() shouldBe listOf(incomeCategory).toJson()
    }

    @Test
    fun `getById happy path`() {
        val request = Request(
            Method.GET,
            "$url/$randomUUID"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe incomeCategory.toJson()
    }

    @Test
    fun `insert happy path`() {
        val request = Request(
            Method.POST,
            url
        ).with(incomeCategoryApi.lens of incomeCategory)

        val result = client(request)

        result.status shouldBe Status.CREATED
        result.bodyString() shouldBe incomeCategory.toJson()
    }

    @Test
    fun `updateById happy path`() {
        val request = Request(
            Method.PUT,
            "$url/$randomUUID"
        ).with(incomeCategoryApi.lens of incomeCategory)

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe incomeCategory.toJson()
    }
}
