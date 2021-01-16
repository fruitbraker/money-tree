package moneytree.persist.repository

import moneytree.domain.entity.Expense as ExpenseDomain
import java.util.UUID
import moneytree.domain.ExpenseSummaryRepository
import moneytree.domain.Repository
import moneytree.domain.entity.ExpenseSummary
import moneytree.libs.commons.result.Result
import moneytree.libs.commons.result.resultTry
import moneytree.persist.db.generated.Tables.EXPENSE
import moneytree.persist.db.generated.Tables.EXPENSE_CATEGORY
import moneytree.persist.db.generated.Tables.VENDOR
import moneytree.persist.db.generated.tables.daos.ExpenseDao
import moneytree.persist.db.generated.tables.pojos.Expense
import org.jooq.Record
class ExpenseRepository(
    private val expenseDao: ExpenseDao
) : Repository<ExpenseDomain>, ExpenseSummaryRepository {

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
            expenseCategory = this[EXPENSE_CATEGORY.NAME],
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
                    newEntity.notes,
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
                    updatedEntity.notes,
                    updatedEntity.hide
                )
                .onDuplicateKeyUpdate()
                .set(EXPENSE.TRANSACTION_DATE, updatedEntity.transactionDate)
                .set(EXPENSE.TRANSACTION_AMOUNT, updatedEntity.transactionAmount)
                .set(EXPENSE.VENDOR, updatedEntity.vendor)
                .set(EXPENSE.EXPENSE_CATEGORY, updatedEntity.expenseCategory)
                .set(EXPENSE.NOTES, updatedEntity.notes)
                .set(EXPENSE.HIDE, updatedEntity.hide)
                .execute()

            updatedEntity.copy(id = uuid)
        }
    }

    override fun getExpenseSummary(): Result<List<ExpenseSummary>, Throwable> {
        return resultTry {
            val result = expenseDao.configuration().dsl()
                .select()
                .from(EXPENSE)
                .join(VENDOR).on(EXPENSE.VENDOR.eq(VENDOR.ID))
                .join(EXPENSE_CATEGORY).on(EXPENSE.EXPENSE_CATEGORY.eq(EXPENSE_CATEGORY.ID))
                .limit(100)
                .fetch()

            result.mapNotNull { it.toSummaryDomain() }
        }
    }

    override fun getExpenseSummaryById(uuid: UUID): Result<ExpenseSummary?, Throwable> {
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
}