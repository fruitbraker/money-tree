package moneytree

import moneytree.api.ExpenseApi
import moneytree.api.ExpenseCategoryApi
import moneytree.api.IncomeCategoryApi
import moneytree.api.VendorApi
import moneytree.libs.http4k.buildRoutes
import moneytree.persist.PersistConnector
import moneytree.persist.db.generated.tables.daos.ExpenseCategoryDao
import moneytree.persist.db.generated.tables.daos.ExpenseDao
import moneytree.persist.db.generated.tables.daos.IncomeCategoryDao
import moneytree.persist.db.generated.tables.daos.VendorDao
import moneytree.persist.repository.ExpenseCategoryRepository
import moneytree.persist.repository.ExpenseRepository
import moneytree.persist.repository.IncomeCategoryRepository
import moneytree.persist.repository.VendorRepository
import moneytree.validator.ExpenseCategoryValidator
import moneytree.validator.ExpenseValidator
import moneytree.validator.IncomeCategoryValidator
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
    private val expenseRepository = ExpenseRepository(
        ExpenseDao(persistConnector.dslContext.configuration())
    )
    private val incomeCategoryRepository = IncomeCategoryRepository(
        IncomeCategoryDao(persistConnector.dslContext.configuration())
    )

    // Validator
    private val expenseCategoryValidator = ExpenseCategoryValidator()
    private val vendorValidator = VendorValidator()
    private val expenseValidator = ExpenseValidator()
    private val incomeCategoryValidator = IncomeCategoryValidator()

    // Routes for entities
    private val expenseCategoryApi = ExpenseCategoryApi(
        expenseCategoryRepository,
        expenseCategoryValidator
    )
    private val vendorApi = VendorApi(
        vendorRepository,
        vendorValidator
    )
    private val expenseApi = ExpenseApi(
        expenseRepository,
        expenseValidator
    )
    private val incomeCategoryApi = IncomeCategoryApi(
        incomeCategoryRepository,
        incomeCategoryValidator
    )

    private val http4kServer: Http4kServer = buildRoutes(
        listOf(
            expenseCategoryApi.makeRoutes(),
            vendorApi.makeRoutes(),
            expenseApi.makeRoutes(),
            incomeCategoryApi.makeRoutes()
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
