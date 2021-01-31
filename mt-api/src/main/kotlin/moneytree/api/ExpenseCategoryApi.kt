package moneytree.api

import moneytree.MtApiRoutes
import moneytree.domain.Repository
import moneytree.domain.entity.ExpenseCategory
import moneytree.validator.Validator
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

class ExpenseCategoryApi(
    repository: Repository<ExpenseCategory>,
    validator: Validator<ExpenseCategory>
) : MtApiRoutes<ExpenseCategory>(
    repository,
    validator
) {
    override val lens: BiDiBodyLens<ExpenseCategory> = Body.auto<ExpenseCategory>().toLens()
    override val listLens: BiDiBodyLens<List<ExpenseCategory>> = Body.auto<List<ExpenseCategory>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/category/expense" bind Method.GET to ::get,
            "/category/expense/{id}" bind Method.GET to ::getById,
            "/category/expense" bind Method.POST to ::insert,
            "/category/expense/{id}" bind Method.PUT to ::upsertById,
            "/category/expense/{id}" bind Method.DELETE to ::deleteById
        )
    }
}
