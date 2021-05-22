package moneytree.api

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import java.time.LocalDate
import java.util.UUID
import moneytree.api.contracts.RoutesTest
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.IncomeCategory
import moneytree.domain.entity.IncomeCategoryFilter
import moneytree.domain.entity.IncomeCategorySummary
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.IncomeCategoryRepository
import moneytree.validator.IncomeCategoryValidator
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class IncomeCategoryApiTest : RoutesTest<IncomeCategory>() {

    private val randomUUID = UUID.randomUUID()
    private val randomString = randomString()

    override val entity = IncomeCategory(
        id = randomUUID,
        name = randomString,
    )

    private val entitySummary = IncomeCategorySummary(
        id = randomUUID,
        name = randomString,
        totalAmount = randomBigDecimal()
    )

    override val entityRepository = mockkClass(IncomeCategoryRepository::class)
    private val entitySummaryRepository = entityRepository as SummaryRepository<IncomeCategorySummary, IncomeCategoryFilter>

    override val entityValidator = IncomeCategoryValidator()
    override val entityApi = IncomeCategoryApi(entitySummaryRepository, entityRepository, entityValidator)

    override val entityPath = "/category/income"
    private val entitySummaryPath = "/category/income/summary"

    private val summaryPathUri: String
        get() = "$url$entitySummaryPath"

    private val incomeCategoryFilter = IncomeCategoryFilter(
        ids = listOf(randomUUID),
        startDate = LocalDate.now().minusDays(1),
        endDate = LocalDate.now(),
    )

    @BeforeAll
    override fun start() {
        every { entitySummaryRepository.getSummary(incomeCategoryFilter) } returns listOf(entitySummary).toOk()

        super.start()
    }

    @Test
    fun `getSummary happy path`() {
        val request = Request(
            Method.GET,
            "$summaryPathUri?startDate=${incomeCategoryFilter.startDate}&" +
                "endDate=${incomeCategoryFilter.endDate}&" +
                "ids=${incomeCategoryFilter.ids.joinToString(",")}"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(entitySummary).toJson()
    }
}
