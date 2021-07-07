package moneytree.api

import java.time.LocalDate
import moneytree.MtApiRoutes
import moneytree.domain.DEFAULT_MINUS_MONTHS
import moneytree.domain.Repository
import moneytree.domain.expense.Expense
import moneytree.domain.expense.ExpenseSummary
import moneytree.domain.expense.ExpenseSummaryFilter
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
    repository: Repository<Expense>,
    validator: Validator<Expense>
) : MtApiRoutes<Expense>(
    repository,
    validator
) {
    private val queryStartDate = Query.localDate().optional("startDate")
    private val queryEndDate = Query.localDate().optional("endDate")
    private val queryVendors = Query.string().optional("vendors")
    private val queryExpenseCategories = Query.string().optional("expenseCategories")

    override val lens: BiDiBodyLens<Expense> = Http4kJackson.autoBody<Expense>().toLens()
    override val listLens: BiDiBodyLens<List<Expense>> = Http4kJackson.autoBody<List<Expense>>().toLens()

    private val summaryListLens: BiDiBodyLens<List<ExpenseSummary>> = Http4kJackson.autoBody<List<ExpenseSummary>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/expense" bind Method.GET to this::get,
            "/expense/{id}" bind Method.GET to ::getById,
            "/expense" bind Method.POST to ::insert,
            "/expense/{id}" bind Method.PUT to ::upsertById,
            "/expense/{id}" bind Method.DELETE to ::deleteById
        )
    }

    override fun get(request: Request): Response {
        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = queryStartDate(request) ?: LocalDate.now().minusMonths(DEFAULT_MINUS_MONTHS),
            endDate = queryEndDate(request) ?: LocalDate.now(),
            vendorIds = queryVendors(request)?.split(',')?.toUUIDList() ?: emptyList(),
            expenseCategoryIds = queryExpenseCategories(request)?.split(',')?.toUUIDList() ?: emptyList()
        )

        return processGetResult(repository.get(expenseSummaryFilter), summaryListLens)
    }
}
