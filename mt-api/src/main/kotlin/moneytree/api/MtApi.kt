package moneytree.api

import moneytree.libs.http4k.buildRoutes
import moneytree.persist.ExpenseCategoryRepository
import moneytree.persist.PersistConnector
import moneytree.persist.VendorRepository
import moneytree.persist.db.generated.tables.daos.ExpenseCategoryDao
import moneytree.persist.db.generated.tables.daos.VendorDao
import moneytree.validator.ExpenseCategoryValidator
import moneytree.validator.VendorValidator
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

class MtApi : AutoCloseable {
    // Database connector
    private val persistConnector = PersistConnector()

    // Data access
    private val expenseCategoryRepository = ExpenseCategoryRepository(
        ExpenseCategoryDao(persistConnector.dslContext.configuration())
    )
    private val vendorRepository = VendorRepository(
        VendorDao(persistConnector.dslContext.configuration())
    )

    // Validator
    private val expenseCategoryValidator = ExpenseCategoryValidator()
    private val vendorValidator = VendorValidator()

    // Routes for entities
    private val expenseCategoryApi = ExpenseCategoryApi(
        expenseCategoryRepository,
        expenseCategoryValidator
    )
    private val vendorApi = VendorApi(
        vendorRepository,
        vendorValidator
    )

    private val http4kServer: Http4kServer = buildRoutes(
        listOf(
            expenseCategoryApi.makeRoutes(),
            vendorApi.makeRoutes()
        )
    ).asServer(Jetty(9000))

    fun start() {
        http4kServer.start()
        println("Starting server at: http://localhost:${http4kServer.port()}")
    }

    override fun close() {
        http4kServer.close()
        persistConnector.close()
    }
}

fun main() {
    MtApi().start()
}
