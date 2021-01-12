package moneytree.api

import moneytree.MtApiRoutes
import moneytree.domain.Repository
import moneytree.domain.entity.Vendor
import moneytree.validator.Validator
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

class VendorApi(
    repository: Repository<Vendor>,
    validator: Validator<Vendor>
) : MtApiRoutes<Vendor>(
    repository,
    validator
) {
    override val lens: BiDiBodyLens<Vendor> = Body.auto<Vendor>().toLens()
    override val listLens: BiDiBodyLens<List<Vendor>> = Body.auto<List<Vendor>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/vendor" bind Method.GET to ::get,
            "/vendor/{id}" bind Method.GET to ::getById,
            "/vendor" bind Method.POST to ::insert,
            "/vendor/{id}" bind Method.PUT to ::upsertById
        )
    }
}
