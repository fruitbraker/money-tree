package moneytree

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import moneytree.api.ExpenseApi
import moneytree.api.ExpenseCategoryApi
import moneytree.api.IncomeApi
import moneytree.api.IncomeCategoryApi
import moneytree.api.VendorApi
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.ExpenseSummary
import moneytree.domain.entity.IncomeSummary
import moneytree.libs.http4k.buildRoutes
import moneytree.persist.PersistConnector
import moneytree.persist.db.generated.tables.daos.ExpenseCategoryDao
import moneytree.persist.db.generated.tables.daos.ExpenseDao
import moneytree.persist.db.generated.tables.daos.IncomeCategoryDao
import moneytree.persist.db.generated.tables.daos.IncomeDao
import moneytree.persist.db.generated.tables.daos.VendorDao
import moneytree.persist.repository.ExpenseCategoryRepository
import moneytree.persist.repository.ExpenseRepository
import moneytree.persist.repository.IncomeCategoryRepository
import moneytree.persist.repository.IncomeRepository
import moneytree.persist.repository.VendorRepository
import moneytree.validator.ExpenseCategoryValidator
import moneytree.validator.ExpenseValidator
import moneytree.validator.IncomeCategoryValidator
import moneytree.validator.IncomeValidator
import moneytree.validator.VendorValidator
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

class MtApi(
    private val config: Config
) : AutoCloseable {
    constructor(configName: String): this(ConfigFactory.load(configName))

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
    private val incomeRepository = IncomeRepository(
        IncomeDao(persistConnector.dslContext.configuration())
    )

    // Validator
    private val expenseCategoryValidator = ExpenseCategoryValidator()
    private val vendorValidator = VendorValidator()
    private val expenseValidator = ExpenseValidator()
    private val incomeCategoryValidator = IncomeCategoryValidator()
    private val incomeValidator = IncomeValidator()

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
        expenseRepository as SummaryRepository<ExpenseSummary>,
        expenseRepository,
        expenseValidator
    )
    private val incomeCategoryApi = IncomeCategoryApi(
        incomeCategoryRepository,
        incomeCategoryValidator
    )
    private val incomeApi = IncomeApi(
        incomeRepository as SummaryRepository<IncomeSummary>,
        incomeRepository,
        incomeValidator
    )

    private val http4kServer: Http4kServer = buildRoutes(
        listOf(
            expenseCategoryApi.makeRoutes(),
            vendorApi.makeRoutes(),
            expenseApi.makeRoutes(),
            incomeCategoryApi.makeRoutes(),
            incomeApi.makeRoutes()
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
    MtApi("defaults.conf").start()
}
