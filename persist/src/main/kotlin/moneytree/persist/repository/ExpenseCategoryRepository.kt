package moneytree.persist.repository

import moneytree.domain.entity.ExpenseCategory as ExpenseCategoryDomain
import java.util.UUID
import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.ExpenseCategoryFilter
import moneytree.domain.entity.ExpenseCategorySummary
import moneytree.libs.commons.result.Result
import moneytree.libs.commons.result.resultTry
import moneytree.libs.commons.result.toOk
import moneytree.persist.db.generated.Tables.EXPENSE
import moneytree.persist.db.generated.Tables.EXPENSE_CATEGORY
import moneytree.persist.db.generated.tables.daos.ExpenseCategoryDao
import moneytree.persist.db.generated.tables.pojos.ExpenseCategory
import org.jooq.Condition
import org.jooq.Record
import org.jooq.impl.DSL
import org.jooq.impl.DSL.sum

class ExpenseCategoryRepository(
    private val expenseCategoryDao: ExpenseCategoryDao
) : Repository<ExpenseCategoryDomain>, SummaryRepository<ExpenseCategorySummary, ExpenseCategoryFilter> {

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

    private fun Record.toSummaryDomain(): ExpenseCategorySummary {
        return ExpenseCategorySummary(
            id = this[EXPENSE_CATEGORY.ID],
            totalAmount = this[sum(EXPENSE.TRANSACTION_AMOUNT)],
            name = this[EXPENSE_CATEGORY.NAME]
        )
    }

    override fun get(): Result<List<ExpenseCategoryDomain>, Throwable> {
        return resultTry {
            val result = expenseCategoryDao.configuration().dsl()
                .select()
                .from(EXPENSE_CATEGORY)
                .limit(100)
                .fetch()

            result.mapNotNull { it.toDomain() }.sortedBy { it.name }
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

    override fun getSummary(filter: ExpenseCategoryFilter): Result<List<ExpenseCategorySummary>, Throwable> {
        return resultTry {
            val result = expenseCategoryDao.configuration().dsl()
                .select(sum(EXPENSE.TRANSACTION_AMOUNT), EXPENSE_CATEGORY.ID, EXPENSE_CATEGORY.NAME)
                .from(EXPENSE)
                .join(EXPENSE_CATEGORY).on(EXPENSE.EXPENSE_CATEGORY.eq(EXPENSE_CATEGORY.ID))
                .where(filter.toWhereClause())
                .groupBy(EXPENSE_CATEGORY.ID)
                .limit(100)
                .fetch()

            result.mapNotNull { it.toSummaryDomain() }
        }
    }

    private fun ExpenseCategoryFilter.toWhereClause(): Condition {
        var condition = DSL.noCondition()

        condition = condition.and(
            EXPENSE.TRANSACTION_DATE.between(
                this.startDate,
                this.endDate
            )
        )

        if (this.ids.isNotEmpty())
            condition = condition.and(EXPENSE.EXPENSE_CATEGORY.`in`(this.ids))

        return condition
    }
}
