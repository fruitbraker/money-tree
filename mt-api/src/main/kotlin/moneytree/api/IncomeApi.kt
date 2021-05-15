package moneytree.api

import java.time.LocalDate
import moneytree.MtApiRoutesWithSummary
import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.DEFAULT_MINUS_MONTHS
import moneytree.domain.entity.Income
import moneytree.domain.entity.IncomeSummary
import moneytree.domain.entity.IncomeSummaryFilter
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

class IncomeApi(
    incomeSummaryRepository: SummaryRepository<IncomeSummary, IncomeSummaryFilter>,
    repository: Repository<Income>,
    validator: Validator<Income>
) : MtApiRoutesWithSummary<Income, IncomeSummary, IncomeSummaryFilter>(
    repository,
    validator
) {
    private val queryStartDate = Query.localDate().optional("startDate")
    private val queryEndDate = Query.localDate().optional("endDate")
    private val queryIncomeCategories = Query.string().optional("incomeCategories")

    override val lens: BiDiBodyLens<Income> = Http4kJackson.autoBody<Income>().toLens()
    override val listLens: BiDiBodyLens<List<Income>> = Http4kJackson.autoBody<List<Income>>().toLens()

    override val summaryRepository = incomeSummaryRepository
    override val summaryLens: BiDiBodyLens<IncomeSummary> = Http4kJackson.autoBody<IncomeSummary>().toLens()
    override val summaryListLens: BiDiBodyLens<List<IncomeSummary>> = Http4kJackson.autoBody<List<IncomeSummary>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/income/summary" bind Method.GET to this::getSummary,
            "/income/summary/{id}" bind Method.GET to ::getSummaryById,
            "/income" bind Method.GET to ::get,
            "/income/{id}" bind Method.GET to ::getById,
            "/income" bind Method.POST to ::insert,
            "/income/{id}" bind Method.PUT to ::upsertById,
            "/income/{id}" bind Method.DELETE to ::deleteById
        )
    }

    override fun getSummary(request: Request): Response {
        val incomeSummaryFilter = IncomeSummaryFilter(
            startDate = queryStartDate(request) ?: LocalDate.now().minusMonths(DEFAULT_MINUS_MONTHS),
            endDate = queryEndDate(request) ?: LocalDate.now(),
            incomeCategoryIds = queryIncomeCategories(request)?.split(',')?.toUUIDList() ?: emptyList()
        )

        return processGetResult(summaryRepository.getSummary(incomeSummaryFilter), summaryListLens)
    }
}
