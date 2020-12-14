package moneytree.persist.expense

import com.google.inject.Inject
import moneytree.domain.Result
import moneytree.domain.Try
import moneytree.domain.expense.Expense
import moneytree.domain.expense.IExpenseRepository
import moneytree.domain.metadata.Metadata
import moneytree.domain.toOk
import moneytree.libs.commons.serde.jackson
import moneytree.libs.commons.serde.toJson
import moneytree.persist.db.generated.tables.Expense.EXPENSE
import org.jooq.DSLContext
import org.jooq.Record
import java.time.OffsetDateTime

class ExpenseRepository @Inject constructor(
    private val dslContext: DSLContext
) : IExpenseRepository {

    private fun Record.toDomain(): Expense {
        return Expense(
            id = this[EXPENSE.ID],
            transactionDate = this[EXPENSE.TRANSACTION_DATE],
            transactionAmount = this[EXPENSE.TRANSACTION_AMOUNT],
            vendor = this[EXPENSE.VENDOR],
            category = this[EXPENSE.CATEGORY],
            metadata = jackson.readValue(this[EXPENSE.METADATA], Metadata::class.java),
            hide = this[EXPENSE.HIDE]
        )
    }

    override fun get(): Result<List<Expense>, Throwable> {
        return Try {
            val result = dslContext.configuration().dsl()
                .select()
                .from(EXPENSE)
                .limit(100)
                .fetch()

            result.mapNotNull { it.toDomain() }
        }
    }

    override fun get(id: Int): Result<Expense, Throwable> {
        return Try {
            val result = dslContext.configuration().dsl()
                .select()
                .from(EXPENSE)
                .where(EXPENSE.ID.eq(id))
                .fetch()

            result.first().toDomain()
        }
    }

    override fun insert(expense: Expense): Result<Expense, Throwable> {
        return Try {
            val metadata = Metadata(
                notes = expense.metadata.notes,
                dateModified = OffsetDateTime.now(),
                dateCreated = OffsetDateTime.now()
            ).toJson()
            val result = dslContext.configuration().dsl()
                .insertInto(EXPENSE)
                .columns(EXPENSE.TRANSACTION_DATE,
                    EXPENSE.TRANSACTION_AMOUNT,
                    EXPENSE.VENDOR,
                    EXPENSE.CATEGORY,
                    EXPENSE.METADATA,
                    EXPENSE.HIDE)
                .values(expense.transactionDate,
                    expense.transactionAmount,
                    expense.vendor,
                    expense.category,
                    metadata,
                    expense.hide)
                .returning()
                .fetch()

            result.first().toDomain()
        }
    }

    override fun update(expense: Expense): Result<Int, Throwable> {
        return Try {
            val updatedMetadata = expense.metadata.copy(
                dateModified = OffsetDateTime.now()
            ).toJson()
            dslContext.configuration().dsl()
                .update(EXPENSE)
                .set(EXPENSE.TRANSACTION_DATE, expense.transactionDate)
                .set(EXPENSE.TRANSACTION_AMOUNT, expense.transactionAmount)
                .set(EXPENSE.VENDOR, expense.vendor)
                .set(EXPENSE.CATEGORY, expense.category)
                .set(EXPENSE.METADATA, updatedMetadata)
                .set(EXPENSE.HIDE, expense.hide)
                .where(EXPENSE.ID.eq(expense.id))
                .execute()
        }
    }

    override fun delete(id: Int): Result<Int, Throwable> {
        return Try {
            dslContext.configuration().dsl()
                .delete(EXPENSE)
                .where(EXPENSE.ID.eq(id))
                .execute()
        }
    }
}
