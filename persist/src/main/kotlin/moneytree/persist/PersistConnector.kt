package moneytree.persist

import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.MappedSchema
import org.jooq.conf.RenderMapping
import org.jooq.conf.Settings
import org.jooq.impl.DSL

class PersistConnector(
    config: PersistConnectorConfig
) : AutoCloseable {

    data class PersistConnectorConfig(
        val host: String,
        val port: Int,
        val databaseName: String,
        val databaseUsername: String,
        val databasePassword: String,
        val schema: String
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
                MappedSchema().withInput(config.schema).withOutput(config.schema)
            )
        ).withExecuteLogging(true)
        _dslContext = DSL.using(dataSource, SQLDialect.POSTGRES, dslSettings)
        dslContext.setSchema(config.schema)
    }

    override fun close() {
        println("closing hikari datasource")
        dataSource.close()
    }
}
