package moneytree.api.contracts

import io.kotest.matchers.shouldBe
import io.mockk.every
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.ExpenseSummaryFilter
import moneytree.domain.entity.Filter
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

abstract class RoutesWithSummaryTest<T, S, F : Filter> : RoutesTest<T>() {

    abstract val entitySummary: S
    abstract val entitySummaryRepository: SummaryRepository<S, F>

    abstract val entitySummaryPath: String

    abstract val filter: F

    private val summaryUrl
        get() = "$url$entitySummaryPath"

    @BeforeAll
    override fun start() {
        every { entitySummaryRepository.getSummary(filter) } returns listOf(entitySummary).toOk()
        every { entitySummaryRepository.getSummaryById(any()) } returns entitySummary.toOk()

        super.start()
    }

    @Test
    fun `getSummary happy path`() {
        val expenseSummaryFilter = filter as ExpenseSummaryFilter
        val request = Request(
            Method.GET,
            "$summaryUrl?startDate=${expenseSummaryFilter.startDate}&endDate=${expenseSummaryFilter.endDate}&vendors=${
                expenseSummaryFilter.vendorIds?.joinToString(
                    ","
                )
            }&expenseCategories=${expenseSummaryFilter.expenseCategoryIds?.joinToString(",")}"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(entitySummary).toJson()
    }

    @Test
    fun `getSummaryById happy path`() {
        val request = Request(
            Method.GET,
            "$summaryUrl/$randomUUIDParameter"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe entitySummary?.toJson()
    }
}
