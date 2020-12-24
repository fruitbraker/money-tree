package moneytree.api.expense

import moneytree.domain.expense.Expense
import moneytree.libs.http4k.HttpRouting
import moneytree.domain.Repository
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

class ExpenseApi(
    repository: Repository<Expense>
) : HttpRouting<Expense> {
    override val lens = Body.auto<Expense>().toLens()
    override val listLens = Body.auto<List<Expense>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/moneytree/domain/expense" bind Method.GET to this::get,
            "/moneytree/domain/expense/{id}" bind Method.GET to this::getById
        )
    }

    override fun get(request: Request): Response {
        return Response(Status.OK).body("Regular get")
    }

    override fun getById(request: Request): Response {
        val uuid = uuidLens(request)
        return Response(Status.OK).body("Get by id with $uuid")
    }
}
