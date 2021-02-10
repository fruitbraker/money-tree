package moneytree.persist

import com.zaxxer.hikari.HikariDataSource
import moneytree.persist.db.generated.Tables
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

class PersistConnector(
    config: PersistConnectorConfig
) : AutoCloseable {

    data class PersistConnectorConfig(
        val host: String,
        val port: Int,
        val databaseName: String,
        val databaseUsername: String,
        val databasePassword: String,
        val createSchema: Boolean,
        val schema: String,
        val createTables: Boolean
    )

    private var _dataSource: HikariDataSource? = null
    private val dataSource: HikariDataSource
        get() = _dataSource ?: throw IllegalStateException("Data source cannot be null!")

    private var _dslContext: DSLContext? = null
    val dslContext: DSLContext
        get() = _dslContext ?: throw IllegalStateException("DSL Context cannot be null!")

    init {
        println("setting up hikari datasource")
        _dataSource = HikariDataSource()

        dataSource.jdbcUrl = "${config.host}:${config.port}/${config.databaseName}"
        dataSource.username = config.databaseUsername
        dataSource.password = config.databasePassword

        println("configuring dsl settings")
        val dslSettings = Settings().withRenderMapping(
            RenderMapping().withSchemata(
                MappedSchema().withInput("mtdev").withOutput(config.schema)
            )
        ).withExecuteLogging(true)
        _dslContext = DSL.using(dataSource, SQLDialect.POSTGRES, dslSettings)
        if (config.createSchema) {
            dslContext.createSchemaIfNotExists(config.schema).execute()
        }

        if (config.createTables) {
            createTables()
        }

        dslContext.setSchema(config.schema)
    }

    private fun createTables() {
        createExpenseCategory()
        createVendor()
        createExpense()
        createIncomeCategory()
        createIncome()
    }

    private fun createExpense() {
        dslContext.createTable("expense")
            .column("id", UUID.nullable(false))
            .column("transaction_date", LOCALDATE.nullable(false))
            .column("transaction_amount", DECIMAL(12, 4).nullable(false))
            .column("vendor", UUID.nullable(false))
            .column("expense_category", UUID.nullable(false))
            .column("notes", VARCHAR(256).nullable(false))
            .column("hide", BOOLEAN.nullable(false))
            .constraints(
                primaryKey("id"),
                foreignKey("vendor").references(Tables.VENDOR),
                foreignKey("expense_category").references(Tables.EXPENSE_CATEGORY)
            )
            .execute()
    }

    private fun createExpenseCategory() {
        dslContext.createTable("expense_category")
            .column("id", UUID.nullable(false))
            .column("name", VARCHAR(256).nullable(false))
            .column("target_amount", DECIMAL(12, 4).nullable(false))
            .constraints(
                primaryKey("id")
            )
            .execute()
    }

    private fun createVendor() {
        dslContext.createTable("vendor")
            .column("id", UUID.nullable(false))
            .column("name", VARCHAR(256).nullable(false))
            .constraints(
                primaryKey("id")
            )
            .execute()
    }

    private fun createIncomeCategory() {
        dslContext.createTable("income_category")
            .column("id", UUID.nullable(false))
            .column("name", VARCHAR(256).nullable(false))
            .constraints(
                primaryKey("id")
            )
            .execute()
    }

    private fun createIncome() {
        dslContext.createTable("income")
            .column("id", UUID.nullable(false))
            .column("source", VARCHAR(256).nullable(false))
            .column("income_category", UUID.nullable(false))
            .column("transaction_date", LOCALDATE.nullable(false))
            .column("transaction_amount", DECIMAL(12, 4).nullable(false))
            .column("notes", VARCHAR(256).nullable(false))
            .column("hide", BOOLEAN.nullable(false))
            .constraints(
                primaryKey("id"),
                foreignKey("income_category").references(Tables.INCOME_CATEGORY),
            )
            .execute()
    }

    override fun close() {
        println("closing hikari datasource")
        dataSource.close()
    }
}
