package moneytree.api

import moneytree.MtApiRoutes
import moneytree.domain.Repository
import moneytree.domain.entity.Income
import moneytree.validator.Validator
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

class IncomeApi(
    repository: Repository<Income>,
    validator: Validator<Income>
) : MtApiRoutes<Income>(
    repository,
    validator
) {
    override val lens: BiDiBodyLens<Income> = Body.auto<Income>().toLens()
    override val listLens: BiDiBodyLens<List<Income>> = Body.auto<List<Income>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/income" bind Method.GET to ::get,
            "/income/{id}" bind Method.GET to ::getById,
            "/income" bind Method.POST to ::insert,
            "/income/{id}" bind Method.PUT to ::upsertById
        )
    }
}
