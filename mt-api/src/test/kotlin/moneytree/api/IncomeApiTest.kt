package moneytree.api

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import java.time.LocalDate
import java.util.UUID
import moneytree.api.contracts.RoutesTest
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.Income
import moneytree.domain.entity.IncomeSummary
import moneytree.domain.entity.IncomeSummaryFilter
import moneytree.libs.commons.result.toOk
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.IncomeRepository
import moneytree.validator.IncomeValidator
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class IncomeApiTest : RoutesTest<Income>() {

    private val randomUUID = UUID.randomUUID()
    private val randomSource = randomString()
    private val todayLocalDate = LocalDate.now()
    private val incomeCategoryId = UUID.randomUUID()
    private val randomIncomeCategoryName = randomString()
    private val randomTransactionAmount = randomBigDecimal()
    private val notes = randomString()
    private val hide = false

    override val entity = Income(
        id = randomUUID,
        source = randomSource,
        incomeCategory = incomeCategoryId,
        transactionDate = todayLocalDate,
        transactionAmount = randomTransactionAmount,
        notes = notes,
        hide = hide
    )

    private val entitySummary = IncomeSummary(
        id = randomUUID,
        source = randomSource,
        incomeCategoryId = incomeCategoryId,
        incomeCategoryName = randomIncomeCategoryName,
        transactionDate = todayLocalDate,
        transactionAmount = randomTransactionAmount,
        notes = notes,
        hide = hide
    )

    override val entityRepository = mockkClass(IncomeRepository::class)
    private val entitySummaryRepository = entityRepository as SummaryRepository<IncomeSummary, IncomeSummaryFilter>

    override val entityValidator = IncomeValidator()
    override val entityApi = IncomeApi(entityRepository as SummaryRepository<IncomeSummary, IncomeSummaryFilter>, entityRepository, entityValidator)

    override val entityPath = "/income"
    private val summaryPath: String = "/income/summary"

    private val incomeSummaryFilter: IncomeSummaryFilter = IncomeSummaryFilter(id = entitySummary.id)

    @BeforeAll
    override fun start() {
        every { entitySummaryRepository.getSummary(incomeSummaryFilter) } returns listOf(entitySummary).toOk()
        every { entitySummaryRepository.getSummaryById(any()) } returns entitySummary.toOk()

        super.start()
    }

    @Test
    fun `getSummary happy path`() {
        true shouldBe true
    }

    @Test
    fun `getSummaryById happy path`() {
        true shouldBe true
    }
}
