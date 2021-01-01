package moneytree.persist

import com.zaxxer.hikari.HikariDataSource
import moneytree.libs.test.commons.randomString
import moneytree.persist.db.generated.Tables.EXPENSE_CATEGORY
import moneytree.persist.db.generated.Tables.VENDOR
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.MappedSchema
import org.jooq.conf.RenderMapping
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.jooq.impl.DSL.foreignKey
import org.jooq.impl.DSL.primaryKey
import org.jooq.impl.SQLDataType.BOOLEAN
import org.jooq.impl.SQLDataType.DECIMAL
import org.jooq.impl.SQLDataType.LOCALDATE
import org.jooq.impl.SQLDataType.UUID
import org.jooq.impl.SQLDataType.VARCHAR

class PersistConnectorTestHarness : AutoCloseable {

    private var _dataSource: HikariDataSource? = null
    private val dataSource: HikariDataSource
        get() = _dataSource ?: throw IllegalStateException("Data source cannot be null!")

    private var _dslContext: DSLContext? = null
    val dslContext: DSLContext
        get() = _dslContext ?: throw IllegalStateException("DSL Context cannot be null!")

    private var testSchema = "test-${randomString()}"

    init {
        _dataSource = HikariDataSource()
        dataSource.jdbcUrl = "jdbc:postgresql://localhost:15432/moneytree-dev?currentSchema=$testSchema"
        dataSource.username = "postgres"
        dataSource.password = "password"

        val dslSettings = Settings().withRenderMapping(
            RenderMapping().withSchemata(
                MappedSchema().withInput("mtdev").withOutput(testSchema)
            )
        ).withExecuteLogging(true)

        _dslContext = DSL.using(dataSource, SQLDialect.POSTGRES, dslSettings)

        println("Creating test schema $testSchema")
        dslContext.createSchema(testSchema).execute()
        dslContext.setSchema(testSchema).execute()

        createTables()
    }

    private fun createTables() {
        createExpenseCategory()
    }

    private fun createExpense() {
        dslContext.createTable("expense")
            .column("id", UUID)
            .column("transaction_date", LOCALDATE)
            .column("transaction_amount", DECIMAL(12, 4))
            .column("vendor", UUID)
            .column("expense_category", UUID)
            .column("notes", VARCHAR(256))
            .column("hide", BOOLEAN)
            .constraints(
                primaryKey("id"),
                foreignKey("vendor").references(VENDOR),
                foreignKey("expense_category").references(EXPENSE_CATEGORY)
            )
            .execute()
    }

    private fun createExpenseCategory() {
        dslContext.createTable("expense_category")
            .column("id", UUID)
            .column("name", VARCHAR(256))
            .column("target_amount", DECIMAL(12, 4))
            .constraints(
                primaryKey("id"),
            )
            .execute()
    }

    override fun close() {
        println("dropping schema $testSchema")
        dslContext.dropSchema(testSchema)
            .cascade()
            .execute()
        dataSource.close()
    }
}
