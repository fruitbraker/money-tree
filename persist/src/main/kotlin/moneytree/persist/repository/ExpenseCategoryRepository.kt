package moneytree.persist.repository

import moneytree.domain.entity.ExpenseCategory as ExpenseCategoryDomain
import java.util.UUID
import moneytree.domain.Repository
import moneytree.libs.commons.result.Result
import moneytree.libs.commons.result.resultTry
import moneytree.libs.commons.result.toOk
import moneytree.persist.db.generated.Tables.EXPENSE_CATEGORY
import moneytree.persist.db.generated.tables.daos.ExpenseCategoryDao
import moneytree.persist.db.generated.tables.pojos.ExpenseCategory
import org.jooq.Record

class ExpenseCategoryRepository(
    private val expenseCategoryDao: ExpenseCategoryDao
) : Repository<ExpenseCategoryDomain> {

    private fun Record.toDomain(): ExpenseCategoryDomain {
        return ExpenseCategoryDomain(
            id = this[EXPENSE_CATEGORY.ID],
            name = this[EXPENSE_CATEGORY.NAME],
            targetAmount = this[EXPENSE_CATEGORY.TARGET_AMOUNT]
        )
    }

    private fun ExpenseCategory.toDomain(): ExpenseCategoryDomain {
        return ExpenseCategoryDomain(
            id = this.id,
            name = this.name,
            targetAmount = this.targetAmount
        )
    }

    override fun get(): Result<List<ExpenseCategoryDomain>, Throwable> {
        return resultTry {
            val result = expenseCategoryDao.configuration().dsl()
                .select()
                .from(EXPENSE_CATEGORY)
                .limit(100)
                .fetch()

            result.mapNotNull { it.toDomain() }
        }
    }

    override fun getById(uuid: UUID): Result<ExpenseCategoryDomain?, Throwable> {
        return resultTry {
            expenseCategoryDao.fetchOneById(uuid)?.toDomain()
        }
    }

    override fun insert(newEntity: ExpenseCategoryDomain): Result<ExpenseCategoryDomain, Throwable> {
        return resultTry {
            val result = expenseCategoryDao.configuration().dsl()
                .insertInto(EXPENSE_CATEGORY)
                .columns(
                    EXPENSE_CATEGORY.ID,
                    EXPENSE_CATEGORY.NAME,
                    EXPENSE_CATEGORY.TARGET_AMOUNT
                )
                .values(
                    newEntity.id ?: UUID.randomUUID(),
                    newEntity.name,
                    newEntity.targetAmount
                )
                .returning()
                .fetch()

            result.first().toDomain()
        }
    }

    override fun upsertById(updatedEntity: ExpenseCategoryDomain, uuid: UUID): Result<ExpenseCategoryDomain, Throwable> {
        return resultTry {
            expenseCategoryDao.configuration().dsl()
                .insertInto(EXPENSE_CATEGORY)
                .columns(
                    EXPENSE_CATEGORY.ID,
                    EXPENSE_CATEGORY.NAME,
                    EXPENSE_CATEGORY.TARGET_AMOUNT
                )
                .values(
                    uuid,
                    updatedEntity.name,
                    updatedEntity.targetAmount
                )
                .onDuplicateKeyUpdate()
                .set(EXPENSE_CATEGORY.NAME, updatedEntity.name)
                .set(EXPENSE_CATEGORY.TARGET_AMOUNT, updatedEntity.targetAmount)
                .execute()

            updatedEntity.copy(id = uuid)
        }
    }

    override fun deleteById(uuid: UUID): Result<Unit, Throwable> {
        return resultTry {
            expenseCategoryDao.configuration().dsl()
                .delete(EXPENSE_CATEGORY)
                .where(EXPENSE_CATEGORY.ID.eq(uuid))
                .execute()

            Unit.toOk()
        }
    }
}
