package moneytree.api

import java.util.UUID
import moneytree.MtApiRoutes
import moneytree.domain.ExpenseSummaryRepository
import moneytree.domain.Repository
import moneytree.domain.entity.Expense
import moneytree.domain.entity.ExpenseSummary
import moneytree.processGetByIdResult
import moneytree.processGetResult
import moneytree.validator.ValidationResult
import moneytree.validator.Validator
import moneytree.validator.validateUUID
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
    private val expenseSummaryRepository = repository as ExpenseSummaryRepository

    override val lens: BiDiBodyLens<Expense> = Body.auto<Expense>().toLens()
    override val listLens: BiDiBodyLens<List<Expense>> = Body.auto<List<Expense>>().toLens()

    private val summaryLens: BiDiBodyLens<ExpenseSummary> = Body.auto<ExpenseSummary>().toLens()
    private val summaryListLens: BiDiBodyLens<List<ExpenseSummary>> = Body.auto<List<ExpenseSummary>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/expense/summary" bind Method.GET to this::getExpenseSummary,
            "/expense/summary/{id}" bind Method.GET to this::getExpenseSummaryById,
            "/expense" bind Method.GET to ::get,
            "/expense/{id}" bind Method.GET to ::getById,
            "/expense" bind Method.POST to ::insert,
            "/expense/{id}" bind Method.PUT to ::upsertById
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun getExpenseSummary(request: Request): Response {
        return processGetResult(expenseSummaryRepository.getExpenseSummary(), summaryListLens)
    }

    private fun getExpenseSummaryById(request: Request): Response {
        val uuid = idLens(request)
        return when (uuid.validateUUID()) {
            ValidationResult.Accepted -> processGetByIdResult(
                expenseSummaryRepository.getExpenseSummaryById(
                    UUID.fromString(
                        uuid
                    )
                ),
                summaryLens
            )
            ValidationResult.Rejected -> Response(Status.BAD_REQUEST)
        }
    }
}
