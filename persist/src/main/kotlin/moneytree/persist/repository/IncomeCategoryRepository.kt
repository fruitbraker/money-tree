package moneytree.persist.repository

import moneytree.domain.entity.IncomeCategory as IncomeCategoryDomain
import java.util.UUID
import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.IncomeCategoryFilter
import moneytree.domain.entity.IncomeCategorySummary
import moneytree.libs.commons.result.Result
import moneytree.libs.commons.result.resultTry
import moneytree.libs.commons.result.toOk
import moneytree.persist.db.generated.Tables
import moneytree.persist.db.generated.tables.Income.INCOME
import moneytree.persist.db.generated.tables.IncomeCategory.INCOME_CATEGORY
import moneytree.persist.db.generated.tables.daos.IncomeCategoryDao
import moneytree.persist.db.generated.tables.pojos.IncomeCategory
import org.jooq.Condition
import org.jooq.Record
import org.jooq.impl.DSL
import org.jooq.impl.DSL.sum

class IncomeCategoryRepository(
    private val incomeCategoryDao: IncomeCategoryDao
) : Repository<IncomeCategoryDomain>, SummaryRepository<IncomeCategorySummary, IncomeCategoryFilter> {

    private fun Record.toDomain(): IncomeCategoryDomain {
        return IncomeCategoryDomain(
            id = this[INCOME_CATEGORY.ID],
            name = this[INCOME_CATEGORY.NAME]
        )
    }

    private fun IncomeCategory.toDomain(): IncomeCategoryDomain {
        return IncomeCategoryDomain(
            id = this.id,
            name = this.name
        )
    }

    private fun Record.toSummaryDomain(): IncomeCategorySummary {
        return IncomeCategorySummary(
            id = this[INCOME_CATEGORY.ID],
            totalAmount = this[sum(INCOME.TRANSACTION_AMOUNT)],
            name = this[INCOME_CATEGORY.NAME]
        )
    }

    override fun get(): Result<List<IncomeCategoryDomain>, Throwable> {
        return resultTry {
            val result = incomeCategoryDao.configuration().dsl()
                .select()
                .from(INCOME_CATEGORY)
                .limit(100)
                .fetch()

            result.mapNotNull { it.toDomain() }.sortedBy { it.name }
        }
    }

    override fun getById(uuid: UUID): Result<IncomeCategoryDomain?, Throwable> {
        return resultTry {
            incomeCategoryDao.fetchOneById(uuid)?.toDomain()
        }
    }

    override fun insert(newEntity: IncomeCategoryDomain): Result<IncomeCategoryDomain, Throwable> {
        return resultTry {
            val result = incomeCategoryDao.configuration().dsl()
                .insertInto(Tables.INCOME_CATEGORY)
                .columns(
                    Tables.INCOME_CATEGORY.ID,
                    Tables.INCOME_CATEGORY.NAME,
                )
                .values(
                    newEntity.id ?: UUID.randomUUID(),
                    newEntity.name,
                )
                .returning()
                .fetch()

            result.first().toDomain()
        }
    }

    override fun upsertById(
        updatedEntity: IncomeCategoryDomain,
        uuid: UUID
    ): Result<IncomeCategoryDomain, Throwable> {
        return resultTry {
            incomeCategoryDao.configuration().dsl()
                .insertInto(Tables.INCOME_CATEGORY)
                .columns(
                    Tables.INCOME_CATEGORY.ID,
                    Tables.INCOME_CATEGORY.NAME
                )
                .values(
                    uuid,
                    updatedEntity.name
                )
                .onDuplicateKeyUpdate()
                .set(Tables.INCOME_CATEGORY.NAME, updatedEntity.name)
                .execute()

            updatedEntity.copy(id = uuid)
        }
    }

    override fun deleteById(uuid: UUID): Result<Unit, Throwable> {
        return resultTry {
            incomeCategoryDao.configuration().dsl()
                .delete(INCOME_CATEGORY)
                .where(INCOME_CATEGORY.ID.eq(uuid))
                .execute()

            Unit.toOk()
        }
    }

    override fun getSummary(filter: IncomeCategoryFilter): Result<List<IncomeCategorySummary>, Throwable> {
        return resultTry {
            val result = incomeCategoryDao.configuration().dsl()
                .select(sum(INCOME.TRANSACTION_AMOUNT), INCOME_CATEGORY.ID, INCOME_CATEGORY.NAME)
                .from(INCOME)
                .join(INCOME_CATEGORY).on(INCOME.INCOME_CATEGORY.eq(INCOME_CATEGORY.ID))
                .where(filter.toWhereClause())
                .groupBy(INCOME_CATEGORY.ID)
                .limit(100)
                .fetch()

            result.mapNotNull { it.toSummaryDomain() }
        }
    }

    private fun IncomeCategoryFilter.toWhereClause(): Condition {
        var condition = DSL.noCondition()

        condition = condition.and(
            INCOME.TRANSACTION_DATE.between(
                this.startDate,
                this.endDate
            )
        )

        if (this.ids.isNotEmpty())
            condition = condition.and(INCOME.INCOME_CATEGORY.`in`(this.ids))

        return condition
    }
}
