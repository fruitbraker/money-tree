package moneytree.api

import moneytree.MtApiRoutes
import moneytree.domain.Repository
import moneytree.domain.entity.Expense
import moneytree.domain.entity.ExpenseSummary
import moneytree.validator.Validator
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

class ExpenseApi(
    repository: Repository<Expense>,
    validator: Validator<Expense>
) : MtApiRoutes<Expense>(
    repository,
    validator
) {
    override val lens: BiDiBodyLens<Expense> = Body.auto<Expense>().toLens()
    override val listLens: BiDiBodyLens<List<Expense>> = Body.auto<List<Expense>>().toLens()

    private val expenseSummaryLens: BiDiBodyLens<ExpenseSummary> = Body.auto<ExpenseSummary>().toLens()
    private val expenseSummaryListLens: BiDiBodyLens<List<ExpenseSummary>> = Body.auto<List<ExpenseSummary>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/expense" bind Method.GET to ::get,
            "/expense/{id}" bind Method.GET to ::getById,
            "/expense" bind Method.POST to ::insert,
            "/expense/{id}" bind Method.PUT to ::upsertById,
            "/expense/summary/{id}" bind Method.GET to this::getExpenseSummaryById,
            "/expense/summary" bind Method.GET to this::getExpenseSummary
        )
    }

    private fun getExpenseSummaryById(request: Request): Response {
        return Response(Status.OK)
    }

    private fun getExpenseSummary(request: Request): Response {
        return Response(Status.OK)
    }
}
