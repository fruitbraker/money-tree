package moneytree.api

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import moneytree.api.expense.ExpenseRoutes
import moneytree.domain.expense.ExpenseService
import moneytree.domain.expense.IExpenseService
import moneytree.persist.PersistModules
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    setUpServer("mtdev").start()
}

fun setUpServer(schema: String): Http4kServer {
    fun health(): RoutingHttpHandler {
        return routes(
            "/health" bind Method.GET to {
                Response(Status.OK).body("Money tree growing money.")
            }
        )
    }

    val injector: Injector = Guice.createInjector(
        ServiceModules(),
        RouteModules(),
        PersistModules(schema)
    )

    val allRoutes = routes(
        health(),
        injector.getInstance(ExpenseRoutes::class.java).expenseRoutes()
    )

    return allRoutes.asServer(Jetty(9000))
}

class ServiceModules : AbstractModule() {
    override fun configure() {
        bind(IExpenseService::class.java).to(ExpenseService::class.java).asEagerSingleton()
    }
}

class RouteModules : AbstractModule() {
    override fun configure() {
        bind(ExpenseRoutes::class.java).asEagerSingleton()
    }
}
