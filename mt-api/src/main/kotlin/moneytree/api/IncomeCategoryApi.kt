package moneytree.api

import moneytree.MtApiRoutes
import moneytree.domain.Repository
import moneytree.domain.entity.IncomeCategory
import moneytree.validator.Validator
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

class IncomeCategoryApi(
    repository: Repository<IncomeCategory>,
    validator: Validator<IncomeCategory>
) : MtApiRoutes<IncomeCategory>(
    repository,
    validator
) {
    override val lens: BiDiBodyLens<IncomeCategory> = Body.auto<IncomeCategory>().toLens()
    override val listLens: BiDiBodyLens<List<IncomeCategory>> = Body.auto<List<IncomeCategory>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/category/income" bind Method.GET to ::get,
            "/category/income/{id}" bind Method.GET to ::getById,
            "/category/income" bind Method.POST to ::insert,
            "/category/income/{id}" bind Method.PUT to ::upsertById
        )
    }
}
