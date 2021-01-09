package moneytree.api

import moneytree.domain.Repository
import moneytree.domain.Vendor
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
    override val lens: BiDiBodyLens<Vendor>
        get() = Body.auto<Vendor>().toLens()
    override val listLens: BiDiBodyLens<List<Vendor>>
        get() = Body.auto<List<Vendor>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/vendor" bind Method.GET to this::get,
            "/vendor/{id}" bind Method.GET to this::getById,
            "/vendor" bind Method.POST to this::insert,
            "/vendor/{id}" bind Method.PUT to this::upsertById
        )
    }
}
