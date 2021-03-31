package moneytree.api

import io.mockk.mockkClass
import java.time.LocalDate
import java.util.UUID
import moneytree.api.contracts.RoutesWithSummaryTest
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.Income
import moneytree.domain.entity.IncomeSummary
import moneytree.domain.entity.IncomeSummaryFilter
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.IncomeRepository
import moneytree.validator.IncomeValidator

class IncomeApiTest : RoutesWithSummaryTest<Income, IncomeSummary, IncomeSummaryFilter>() {

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

    override val entitySummary = IncomeSummary(
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
    override val entitySummaryRepository = entityRepository as SummaryRepository<IncomeSummary, IncomeSummaryFilter>
    override val entityValidator = IncomeValidator()
    override val entityApi = IncomeApi(entityRepository as SummaryRepository<IncomeSummary, IncomeSummaryFilter>, entityRepository, entityValidator)

    override val entityPath = "/income"
    override val entitySummaryPath: String = "/income/summary"

    override val filter: IncomeSummaryFilter = IncomeSummaryFilter(id = entitySummary.id)
}
