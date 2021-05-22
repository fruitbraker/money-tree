package moneytree.api

import java.time.LocalDate
import moneytree.MtApiRoutesWithSummary
import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.DEFAULT_MINUS_MONTHS
import moneytree.domain.entity.IncomeCategory
import moneytree.domain.entity.IncomeCategoryFilter
import moneytree.domain.entity.IncomeCategorySummary
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

class IncomeCategoryApi(
    incomeCategorySummaryRepository: SummaryRepository<IncomeCategorySummary, IncomeCategoryFilter>,
    repository: Repository<IncomeCategory>,
    validator: Validator<IncomeCategory>
) : MtApiRoutesWithSummary<IncomeCategory, IncomeCategorySummary, IncomeCategoryFilter>(
    repository,
    validator
) {
    private val queryStartDate = Query.localDate().optional("startDate")
    private val queryEndDate = Query.localDate().optional("endDate")
    private val queryExpenseCategoryIds = Query.string().optional("ids")

    override val lens: BiDiBodyLens<IncomeCategory> = Http4kJackson.autoBody<IncomeCategory>().toLens()
    override val listLens: BiDiBodyLens<List<IncomeCategory>> = Http4kJackson.autoBody<List<IncomeCategory>>().toLens()

    override val summaryRepository: SummaryRepository<IncomeCategorySummary, IncomeCategoryFilter> = incomeCategorySummaryRepository
    override val summaryLens: BiDiBodyLens<IncomeCategorySummary> = Http4kJackson.autoBody<IncomeCategorySummary>().toLens()
    override val summaryListLens: BiDiBodyLens<List<IncomeCategorySummary>> = Http4kJackson.autoBody<List<IncomeCategorySummary>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/category/income/summary" bind Method.GET to this::getSummary,
            "/category/income" bind Method.GET to ::get,
            "/category/income/{id}" bind Method.GET to ::getById,
            "/category/income" bind Method.POST to ::insert,
            "/category/income/{id}" bind Method.PUT to ::upsertById,
            "/category/income/{id}" bind Method.DELETE to ::deleteById
        )
    }

    override fun getSummary(request: Request): Response {
        val incomeCategoryFilter = IncomeCategoryFilter(
            ids = queryExpenseCategoryIds(request)?.split(',')?.toUUIDList() ?: emptyList(),
            startDate = queryStartDate(request) ?: LocalDate.now().minusMonths(DEFAULT_MINUS_MONTHS),
            endDate = queryEndDate(request) ?: LocalDate.now()
        )

        return processGetResult(summaryRepository.getSummary(incomeCategoryFilter), summaryListLens)
    }
}
