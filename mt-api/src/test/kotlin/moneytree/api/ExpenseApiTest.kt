package moneytree.api

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import java.time.LocalDate
import java.util.UUID
import moneytree.api.contracts.RoutesTest
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.Expense
import moneytree.domain.entity.ExpenseSummary
import moneytree.domain.entity.ExpenseSummaryFilter
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.ExpenseRepository
import moneytree.validator.ExpenseValidator
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ExpenseApiTest : RoutesTest<Expense>() {

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

    private val entitySummary = ExpenseSummary(
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
    private val entitySummaryRepository = entityRepository as SummaryRepository<ExpenseSummary, ExpenseSummaryFilter>

    override val entityValidator = ExpenseValidator()
    override val entityApi = ExpenseApi(
        entityRepository as SummaryRepository<ExpenseSummary, ExpenseSummaryFilter>,
        entityRepository,
        entityValidator
    )

    override val entityPath: String = "/expense"
    private val entitySummaryPath: String = "/expense/summary"

    private val summaryPathUri: String
        get() = "$url$entitySummaryPath"

    private val expenseSummaryFilter: ExpenseSummaryFilter = ExpenseSummaryFilter(
        startDate = LocalDate.now().minusDays(1),
        endDate = LocalDate.now(),
        vendorIds = listOf(entitySummary.vendorId),
        expenseCategoryIds = listOf(entitySummary.expenseCategoryId)
    )

    @BeforeAll
    override fun start() {
        every { entitySummaryRepository.getSummary(expenseSummaryFilter) } returns listOf(entitySummary).toOk()

        super.start()
    }

    @Test
    fun `getSummary happy path`() {
        val request = Request(
            Method.GET,
            "$summaryPathUri?startDate=${expenseSummaryFilter.startDate}&" +
                "endDate=${expenseSummaryFilter.endDate}&" +
                "vendors=${expenseSummaryFilter.vendorIds.joinToString(",")}&" +
                "expenseCategories=${expenseSummaryFilter.expenseCategoryIds.joinToString(",")}"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(entitySummary).toJson()
    }
}
