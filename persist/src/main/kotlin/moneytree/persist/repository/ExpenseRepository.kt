package moneytree.persist.repository

import moneytree.domain.entity.Expense as ExpenseDomain
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.ExpenseSummary
import moneytree.domain.entity.ExpenseSummaryFilter
import moneytree.libs.commons.result.Result
import moneytree.libs.commons.result.resultTry
import moneytree.libs.commons.result.toOk
import moneytree.persist.db.generated.Tables.EXPENSE
import moneytree.persist.db.generated.Tables.EXPENSE_CATEGORY
import moneytree.persist.db.generated.Tables.VENDOR
import moneytree.persist.db.generated.tables.daos.ExpenseDao
import moneytree.persist.db.generated.tables.pojos.Expense
import org.jooq.Condition
import org.jooq.Record
import org.jooq.impl.DSL

class ExpenseRepository(
    private val expenseDao: ExpenseDao
) : Repository<ExpenseDomain>, SummaryRepository<ExpenseSummary, ExpenseSummaryFilter> {

    private fun Record.toDomain(): ExpenseDomain {
        return ExpenseDomain(
            id = this[EXPENSE.ID],
            transactionDate = this[EXPENSE.TRANSACTION_DATE],
            transactionAmount = this[EXPENSE.TRANSACTION_AMOUNT],
            vendor = this[EXPENSE.VENDOR],
            expenseCategory = this[EXPENSE.EXPENSE_CATEGORY],
            notes = this[EXPENSE.NOTES],
            hide = this[EXPENSE.HIDE]
        )
    }

    private fun Expense.toDomain(): ExpenseDomain {
        return ExpenseDomain(
            id = this.id,
            transactionDate = this.transactionDate,
            transactionAmount = this.transactionAmount,
            vendor = this.vendor,
            expenseCategory = this.expenseCategory,
            notes = this.notes,
            hide = this.hide
        )
    }

    private fun Record.toSummaryDomain(): ExpenseSummary {
        return ExpenseSummary(
            id = this[EXPENSE.ID],
            transactionDate = this[EXPENSE.TRANSACTION_DATE],
            transactionAmount = this[EXPENSE.TRANSACTION_AMOUNT],
            vendorId = this[VENDOR.ID],
            vendorName = this[VENDOR.NAME],
            expenseCategoryId = this[EXPENSE_CATEGORY.ID],
            expenseCategoryName = this[EXPENSE_CATEGORY.NAME],
            notes = this[EXPENSE.NOTES],
            hide = this[EXPENSE.HIDE]
        )
    }

    override fun get(): Result<List<ExpenseDomain>, Throwable> {
        return resultTry {
            val result = expenseDao.configuration().dsl()
                .select()
                .from(EXPENSE)
                .limit(100)
                .fetch()

            result.mapNotNull { it.toDomain() }
        }
    }

    override fun getById(uuid: UUID): Result<ExpenseDomain?, Throwable> {
        return resultTry {
            expenseDao.fetchOneById(uuid)?.toDomain()
        }
    }

    override fun insert(newEntity: ExpenseDomain): Result<ExpenseDomain, Throwable> {
        return resultTry {
            val result = expenseDao.configuration().dsl()
                .insertInto(EXPENSE)
                .columns(
                    EXPENSE.ID,
                    EXPENSE.TRANSACTION_DATE,
                    EXPENSE.TRANSACTION_AMOUNT,
                    EXPENSE.VENDOR,
                    EXPENSE.EXPENSE_CATEGORY,
                    EXPENSE.NOTES,
                    EXPENSE.HIDE
                )
                .values(
                    newEntity.id ?: UUID.randomUUID(),
                    newEntity.transactionDate,
                    newEntity.transactionAmount,
                    newEntity.vendor,
                    newEntity.expenseCategory,
                    newEntity.notes.trim(),
                    newEntity.hide
                )
                .returning()
                .fetch()

            result.first().toDomain()
        }
    }

    override fun upsertById(updatedEntity: ExpenseDomain, uuid: UUID): Result<ExpenseDomain, Throwable> {
        return resultTry {
            expenseDao.configuration().dsl()
                .insertInto(EXPENSE)
                .columns(
                    EXPENSE.ID,
                    EXPENSE.TRANSACTION_DATE,
                    EXPENSE.TRANSACTION_AMOUNT,
                    EXPENSE.VENDOR,
                    EXPENSE.EXPENSE_CATEGORY,
                    EXPENSE.NOTES,
                    EXPENSE.HIDE
                )
                .values(
                    uuid,
                    updatedEntity.transactionDate,
                    updatedEntity.transactionAmount,
                    updatedEntity.vendor,
                    updatedEntity.expenseCategory,
                    updatedEntity.notes.trim(),
                    updatedEntity.hide
                )
                .onDuplicateKeyUpdate()
                .set(EXPENSE.TRANSACTION_DATE, updatedEntity.transactionDate)
                .set(EXPENSE.TRANSACTION_AMOUNT, updatedEntity.transactionAmount)
                .set(EXPENSE.VENDOR, updatedEntity.vendor)
                .set(EXPENSE.EXPENSE_CATEGORY, updatedEntity.expenseCategory)
                .set(EXPENSE.NOTES, updatedEntity.notes.trim())
                .set(EXPENSE.HIDE, updatedEntity.hide)
                .execute()

            updatedEntity.copy(id = uuid)
        }
    }

    override fun getSummary(filter: ExpenseSummaryFilter): Result<List<ExpenseSummary>, Throwable> {
        return resultTry {
            val result = expenseDao.configuration().dsl()
                .select()
                .from(EXPENSE)
                .join(VENDOR).on(EXPENSE.VENDOR.eq(VENDOR.ID))
                .join(EXPENSE_CATEGORY).on(EXPENSE.EXPENSE_CATEGORY.eq(EXPENSE_CATEGORY.ID))
                .where(filter.toWhereClause())
                .limit(100)
                .fetch()

            result.mapNotNull { it.toSummaryDomain() }
                .sortedByDescending { it.transactionDate }
        }
    }

    override fun getSummaryById(uuid: UUID): Result<ExpenseSummary?, Throwable> {
        return resultTry {
            val result = expenseDao.configuration().dsl()
                .select()
                .from(EXPENSE)
                .join(VENDOR).on(EXPENSE.VENDOR.eq(VENDOR.ID))
                .join(EXPENSE_CATEGORY).on(EXPENSE.EXPENSE_CATEGORY.eq(EXPENSE_CATEGORY.ID))
                .where(EXPENSE.ID.eq(uuid))
                .fetch()

            if (result.isNotEmpty)
                result.first().toSummaryDomain()
            else
                null
        }
    }

    override fun deleteById(uuid: UUID): Result<Unit, Throwable> {
        return resultTry {
            expenseDao.configuration().dsl()
                .delete(EXPENSE)
                .where(EXPENSE.ID.eq(uuid))
                .execute()

            Unit.toOk()
        }
    }

    private fun ExpenseSummaryFilter?.toWhereClause(): Condition {
        var condition = DSL.noCondition()

        if (this == null) return condition

        condition = condition.and(
            EXPENSE.TRANSACTION_DATE.between(
                this.startDate ?: LocalDate.parse("1000-01-01"),
                this.endDate ?: LocalDate.now()
            )
        )

        this.expenseCategoryIds?.let {
            condition = condition.and(EXPENSE.EXPENSE_CATEGORY.`in`(it))
        }

        this.vendorIds?.let {
            condition = condition.and(EXPENSE.VENDOR.`in`(it))
        }

        return condition
    }
}
