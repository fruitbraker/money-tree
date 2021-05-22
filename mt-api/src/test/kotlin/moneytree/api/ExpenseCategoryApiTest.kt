package moneytree.api

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import java.time.LocalDate
import java.util.UUID
import moneytree.api.contracts.RoutesTest
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.ExpenseCategory
import moneytree.domain.entity.ExpenseCategoryFilter
import moneytree.domain.entity.ExpenseCategorySummary
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.ExpenseCategoryRepository
import moneytree.validator.ExpenseCategoryValidator
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ExpenseCategoryApiTest : RoutesTest<ExpenseCategory>() {

    private val randomUUID = UUID.randomUUID()
    private val randomString = randomString()
    private val randomBigDecimal = randomBigDecimal()

    override val entity = ExpenseCategory(
        id = randomUUID,
        name = randomString,
        targetAmount = randomBigDecimal
    )

    private val entitySummary = ExpenseCategorySummary(
        id = randomUUID,
        name = randomString,
        totalAmount = randomBigDecimal
    )

    override val entityRepository = mockkClass(ExpenseCategoryRepository::class)
    private val entitySummaryRepository =
        entityRepository as SummaryRepository<ExpenseCategorySummary, ExpenseCategoryFilter>

    override val entityValidator = ExpenseCategoryValidator()
    override val entityApi = ExpenseCategoryApi(
        entitySummaryRepository,
        entityRepository,
        entityValidator
    )

    override val entityPath: String = "/category/expense"
    private val entitySummaryPath: String = "/category/expense/summary"

    private val summaryPathUri: String
        get() = "$url$entitySummaryPath"

    private val expenseCategoryFilter = ExpenseCategoryFilter(
        ids = listOf(randomUUID),
        startDate = LocalDate.now().minusDays(1),
        endDate = LocalDate.now(),
    )

    @BeforeAll
    override fun start() {
        every { entitySummaryRepository.getSummary(expenseCategoryFilter) } returns listOf(entitySummary).toOk()

        super.start()
    }

    @Test
    fun `getSummary happy path`() {
        val request = Request(
            Method.GET,
            "$summaryPathUri?startDate=${expenseCategoryFilter.startDate}&" +
                "endDate=${expenseCategoryFilter.endDate}&" +
                "ids=${expenseCategoryFilter.ids.joinToString(",")}"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(entitySummary).toJson()
    }
}
