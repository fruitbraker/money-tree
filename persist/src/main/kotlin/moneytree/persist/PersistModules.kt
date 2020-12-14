package moneytree.persist

import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Provides
import com.google.inject.Singleton
import com.zaxxer.hikari.HikariDataSource
import moneytree.domain.expense.IExpenseRepository
import moneytree.persist.expense.ExpenseRepository
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.MappedSchema
import org.jooq.conf.RenderMapping
import org.jooq.conf.Settings
import org.jooq.impl.DSL

class PersistModules(val schema: String) : AbstractModule() {
    override fun configure() {
        bind(IExpenseRepository::class.java).to(ExpenseRepository::class.java).asEagerSingleton()
    }

    @Provides
    @Singleton
    fun dbConnection(): HikariDataSource {
        val ds = HikariDataSource()
        ds.jdbcUrl = "jdbc:postgresql://localhost:5432/moneytree?currentSchema=$schema"
        ds.username = "postgres"
        ds.password = "qwertyuiop"
        return ds
    }

    @Provides
    @Singleton
    @Inject
    fun dslContext(ds: HikariDataSource): DSLContext {
        val settings = Settings().withRenderMapping(
            RenderMapping().withSchemata(
                MappedSchema().withInput("mtdev").withOutput(schema)
            )
        ).withExecuteLogging(true)
        return DSL.using(ds, SQLDialect.POSTGRES, settings)
    }
}
