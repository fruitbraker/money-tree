package moneytree.api

import java.time.LocalDate
import moneytree.MtApiRoutesWithSummary
import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.DEFAULT_MINUS_MONTHS
import moneytree.domain.entity.Expense
import moneytree.domain.entity.ExpenseSummary
import moneytree.domain.entity.ExpenseSummaryFilter
import moneytree.libs.http4k.Http4kJackson
import moneytree.processGetResult
import moneytree.validator.Validator
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.Query
import org.http4k.lens.localDate
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import toUUIDList

class ExpenseApi(
    expenseSummaryRepository: SummaryRepository<ExpenseSummary, ExpenseSummaryFilter>,
    repository: Repository<Expense>,
    validator: Validator<Expense>
) : MtApiRoutesWithSummary<Expense, ExpenseSummary, ExpenseSummaryFilter>(
    repository,
    validator
) {
    private val queryStartDate = Query.localDate().optional("startDate")
    private val queryEndDate = Query.localDate().optional("endDate")
    private val queryVendors = Query.string().optional("vendors")
    private val queryExpenseCategories = Query.string().optional("expenseCategories")

    override val lens: BiDiBodyLens<Expense> = Http4kJackson.autoBody<Expense>().toLens()
    override val listLens: BiDiBodyLens<List<Expense>> = Http4kJackson.autoBody<List<Expense>>().toLens()

    override val summaryRepository: SummaryRepository<ExpenseSummary, ExpenseSummaryFilter> = expenseSummaryRepository
    override val summaryListLens: BiDiBodyLens<List<ExpenseSummary>> = Http4kJackson.autoBody<List<ExpenseSummary>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/expense/summary" bind Method.GET to this::getSummary,
            "/expense" bind Method.GET to ::get,
            "/expense/{id}" bind Method.GET to ::getById,
            "/expense" bind Method.POST to ::insert,
            "/expense/{id}" bind Method.PUT to ::upsertById,
            "/expense/{id}" bind Method.DELETE to ::deleteById
        )
    }

    override fun getSummary(request: Request): Response {
        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = queryStartDate(request) ?: LocalDate.now().minusMonths(DEFAULT_MINUS_MONTHS),
            endDate = queryEndDate(request) ?: LocalDate.now(),
            vendorIds = queryVendors(request)?.split(',')?.toUUIDList() ?: emptyList(),
            expenseCategoryIds = queryExpenseCategories(request)?.split(',')?.toUUIDList() ?: emptyList()
        )

        return processGetResult(summaryRepository.getSummary(expenseSummaryFilter), summaryListLens)
    }
}
