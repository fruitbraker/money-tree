package moneytree.persist.repository

import moneytree.domain.entity.IncomeCategory as IncomeCategoryDomain
import java.util.UUID
import moneytree.domain.Repository
import moneytree.libs.commons.result.Result
import moneytree.libs.commons.result.resultTry
import moneytree.persist.db.generated.Tables
import moneytree.persist.db.generated.tables.IncomeCategory.INCOME_CATEGORY
import moneytree.persist.db.generated.tables.daos.IncomeCategoryDao
import moneytree.persist.db.generated.tables.pojos.IncomeCategory
import org.jooq.Record

class IncomeCategoryRepository(
    private val incomeCategoryDao: IncomeCategoryDao
) : Repository<IncomeCategoryDomain> {

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

    override fun get(): Result<List<IncomeCategoryDomain>, Throwable> {
        return resultTry {
            val result = incomeCategoryDao.configuration().dsl()
                .select()
                .from(INCOME_CATEGORY)
                .limit(100)
                .fetch()

            result.mapNotNull { it.toDomain() }
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
}
