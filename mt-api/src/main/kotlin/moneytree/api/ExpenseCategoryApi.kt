package moneytree.api

import moneytree.domain.ExpenseCategory
import moneytree.domain.Repository
import moneytree.libs.commons.result.onOk
import moneytree.libs.http4k.HttpRouting
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

class ExpenseCategoryApi(
    private val repository: Repository<ExpenseCategory>
) : HttpRouting<ExpenseCategory> {

    override val lens: BiDiBodyLens<ExpenseCategory>
        get() = Body.auto<ExpenseCategory>().toLens()
    override val listLens: BiDiBodyLens<List<ExpenseCategory>>
        get() = Body.auto<List<ExpenseCategory>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/expense-category" bind Method.GET to this::get,
            "/expense-category/{id}" bind Method.GET to this::getById
        )
    }

    override fun get(request: Request): Response {
        repository.get().onOk {
            return Response(Status.OK).with(listLens of it)
        }
        return Response(Status.BAD_REQUEST)
    }

    override fun getById(request: Request): Response {
        val uuid = uuidLens(request)
        repository.getById(uuid).onOk { expenseCategory ->
            return when (expenseCategory) {
                null -> Response(Status.NOT_FOUND)
                else -> Response(Status.OK).with(lens of expenseCategory)
            }
        }
        return Response(Status.BAD_REQUEST)
    }
}
