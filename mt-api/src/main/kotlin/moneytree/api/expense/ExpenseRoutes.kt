package moneytree.api.expense

import com.google.inject.Inject
import moneytree.domain.Result
import moneytree.domain.expense.IExpenseService
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Gson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.OffsetDateTime

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
                        println(OffsetDateTime.now())
                        Response(Status.OK).with(expenseListLens of entity)
                    }
                    is Result.Err -> {
                        Response(Status.BAD_GATEWAY).body("Something went wrong")
                    }
                }
            }
        )
    }
}
