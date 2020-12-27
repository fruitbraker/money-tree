package moneytree.api

import moneytree.api.expense.ExpenseApi
import moneytree.libs.http4k.buildRoutes
import moneytree.persist.PersistConnector
import moneytree.persist.expense.ExpenseRepository
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

class MtApi : AutoCloseable {
    // Database connector
    private val persistConnector = PersistConnector()

    // Data access for entities
    private val expenseRepository = ExpenseRepository(persistConnector.dslContext)

    // Routes for entities
    private val expenseApi = ExpenseApi(expenseRepository)

    private val http4kServer: Http4kServer = buildRoutes(
        listOf(
            expenseApi.makeRoutes()
        )
    ).asServer(Jetty(9000))

    fun start() {
        http4kServer.start()
    }

    override fun close() {
        http4kServer.close()
        persistConnector.close()
    }
}

fun main() {
    MtApi().start()
}
