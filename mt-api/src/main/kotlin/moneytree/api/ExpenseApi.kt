package moneytree.api

import moneytree.MtApiRoutesWithSummary
import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.Expense
import moneytree.domain.entity.ExpenseSummary
import moneytree.validator.Validator
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

class ExpenseApi(
    expenseSummaryRepository: SummaryRepository<ExpenseSummary>,
    repository: Repository<Expense>,
    validator: Validator<Expense>
) : MtApiRoutesWithSummary<Expense, ExpenseSummary>(
    repository,
    validator
) {
    override val lens: BiDiBodyLens<Expense> = Body.auto<Expense>().toLens()
    override val listLens: BiDiBodyLens<List<Expense>> = Body.auto<List<Expense>>().toLens()

    override val summaryRepository = expenseSummaryRepository
    override val summaryLens: BiDiBodyLens<ExpenseSummary> = Body.auto<ExpenseSummary>().toLens()
    override val summaryListLens: BiDiBodyLens<List<ExpenseSummary>> = Body.auto<List<ExpenseSummary>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/expense/summary" bind Method.GET to ::getSummary,
            "/expense/summary/{id}" bind Method.GET to ::getSummaryById,
            "/expense" bind Method.GET to ::get,
            "/expense/{id}" bind Method.GET to ::getById,
            "/expense" bind Method.POST to ::insert,
            "/expense/{id}" bind Method.PUT to ::upsertById,
            "/expense/{id}" bind Method.DELETE to ::deleteById
        )
    }
}
