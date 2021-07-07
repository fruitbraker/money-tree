package moneytree.api

import java.time.LocalDate
import moneytree.MtApiRoutesWithSummary
import moneytree.domain.DEFAULT_MINUS_MONTHS
import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.ExpenseCategory
import moneytree.domain.entity.ExpenseCategoryFilter
import moneytree.domain.entity.ExpenseCategorySummary
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

class ExpenseCategoryApi(
    expenseCategorySummaryRepository: SummaryRepository<ExpenseCategorySummary, ExpenseCategoryFilter>,
    repository: Repository<ExpenseCategory>,
    validator: Validator<ExpenseCategory>
) : MtApiRoutesWithSummary<ExpenseCategory, ExpenseCategorySummary, ExpenseCategoryFilter>(
    repository,
    validator
) {
    private val queryStartDate = Query.localDate().optional("startDate")
    private val queryEndDate = Query.localDate().optional("endDate")
    private val queryExpenseCategoryIds = Query.string().optional("ids")

    override val lens: BiDiBodyLens<ExpenseCategory> = Http4kJackson.autoBody<ExpenseCategory>().toLens()
    override val listLens: BiDiBodyLens<List<ExpenseCategory>> = Http4kJackson.autoBody<List<ExpenseCategory>>().toLens()

    override val summaryRepository: SummaryRepository<ExpenseCategorySummary, ExpenseCategoryFilter> = expenseCategorySummaryRepository
    override val summaryListLens: BiDiBodyLens<List<ExpenseCategorySummary>> = Http4kJackson.autoBody<List<ExpenseCategorySummary>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/category/expense/summary" bind Method.GET to this::getSummary,
            "/category/expense" bind Method.GET to ::get,
            "/category/expense/{id}" bind Method.GET to ::getById,
            "/category/expense" bind Method.POST to ::insert,
            "/category/expense/{id}" bind Method.PUT to ::upsertById,
            "/category/expense/{id}" bind Method.DELETE to ::deleteById
        )
    }

    override fun getSummary(request: Request): Response {
        val expenseCategoryFilter = ExpenseCategoryFilter(
            ids = queryExpenseCategoryIds(request)?.split(',')?.toUUIDList() ?: emptyList(),
            startDate = queryStartDate(request) ?: LocalDate.now().minusMonths(DEFAULT_MINUS_MONTHS),
            endDate = queryEndDate(request) ?: LocalDate.now()
        )

        return processGetResult(summaryRepository.getSummary(expenseCategoryFilter), summaryListLens)
    }
}
