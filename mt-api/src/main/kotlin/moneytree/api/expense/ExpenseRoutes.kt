package moneytree.api.expense

import com.google.inject.Inject
import moneytree.api.common.intLens
import moneytree.domain.Result
import moneytree.domain.expense.IExpenseService
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.util.*

class ExpenseRoutes @Inject constructor(
    private val expenseService: IExpenseService
) {
    fun expenseRoutes(): RoutingHttpHandler {
        val expenseLens = Body.auto<Expense>().toLens()
        val expenseListLens = Body.auto<List<Expense>>().toLens()

        return routes(
            "/expenses" bind Method.GET to {
                when (val result = expenseService.get()) {
                    is Result.Ok -> {
                        val entity = result.value.map {
                           Expense.fromDomain(it)
                        }
                        Response(Status.OK).with(expenseListLens of entity)
                    }
                    is Result.Err -> {
                        Response(Status.BAD_GATEWAY)
                    }
                }
            },
            "/expenses" bind Method.POST to {
                val newExpense = expenseLens(it)
                when (val result = expenseService.insert(Expense.toDomain(newExpense))) {
                    is Result.Ok -> {
                        val entity = Expense.fromDomain(result.value)
                        Response(Status.CREATED).with(expenseLens of entity)
                    }
                    is Result.Err -> {
                        Response(Status.BAD_REQUEST)
                    }
                }
            },
            "/expenses/{id}" bind Method.GET to {
                val id = intLens(it)
                when (val result = expenseService.get(id)) {
                    is Result.Ok -> {
                        val entity = Expense.fromDomain(result.value)
                        Response(Status.OK).with(expenseLens of entity)
                    }
                    is Result.Err -> {
                        when (result.error) {
                            is NoSuchElementException -> Response(Status.NOT_FOUND)
                            else -> Response(Status.BAD_REQUEST)
                        }
                    }
                }
            },
            "/expenses/{id}" bind Method.PATCH to {
                val idPath = intLens(it)
                val inboundExpense = expenseLens(it).copy(
                    id = idPath
                )
                when (val result = expenseService.update(Expense.toDomain(inboundExpense))) {
                    is Result.Ok -> {
                        if (result.value > 0)
                            Response(Status.NO_CONTENT)
                        else
                            Response(Status.BAD_REQUEST)
                    }
                    is Result.Err -> {
                        // TODO: Resource conflict error
                        println(result.error)
                        when (result.error) {
                            is NoSuchElementException -> Response(Status.NOT_FOUND)
                            else -> Response(Status.BAD_REQUEST)
                        }
                    }
                }
            },
            "/expenses/{id}" bind Method.DELETE to {
                val idPath = intLens(it)
                when (val result = expenseService.delete(idPath)) {
                    is Result.Ok -> {
                        if (result.value > 0)
                            Response(Status.NO_CONTENT)
                        else
                            Response(Status.BAD_REQUEST)
                    }
                    is Result.Err -> {
                        when (result.error) {
                            is NoSuchElementException -> Response(Status.NOT_FOUND)
                            else -> Response(Status.BAD_REQUEST)
                        }
                    }
                }
            }
        )
    }
}
