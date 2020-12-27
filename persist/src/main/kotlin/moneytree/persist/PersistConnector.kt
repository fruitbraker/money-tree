package moneytree.persist

import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.MappedSchema
import org.jooq.conf.RenderMapping
import org.jooq.conf.Settings
import org.jooq.impl.DSL

class PersistConnector : AutoCloseable {

    private var _dataSource: HikariDataSource? = null
    private val dataSource: HikariDataSource
        get() =_dataSource ?: throw IllegalStateException("Data source cannot be null!")

    private var _dslContext: DSLContext? = null
    val dslContext: DSLContext
        get() = _dslContext ?: throw java.lang.IllegalStateException("DSL Context cannot be null!")

    init {
        println("setting up hikari datasource")
        _dataSource = HikariDataSource()
        dataSource.jdbcUrl = "jdbc:postgresql://localhost:15432/moneytree-dev?currentSchema=mtdev"
        dataSource.username = "postgres"
        dataSource.password = "password"

        println("configuring dsl settings")
        val dslSettings = Settings().withRenderMapping(
            RenderMapping().withSchemata(
                MappedSchema().withInput("mtdev").withOutput("mtdev")
            )
        ).withExecuteLogging(true)
        _dslContext = DSL.using(dataSource, SQLDialect.POSTGRES, dslSettings)
    }

    override fun close() {
        println("closing hikdari datasource")
        dataSource.close()
    }
}
