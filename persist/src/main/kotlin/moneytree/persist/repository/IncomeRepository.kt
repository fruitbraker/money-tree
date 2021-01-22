package moneytree.persist.repository

import moneytree.domain.entity.Income as IncomeDomain
import java.util.UUID
import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.IncomeSummary
import moneytree.libs.commons.result.Result
import moneytree.libs.commons.result.resultTry
import moneytree.persist.db.generated.tables.Income.INCOME
import moneytree.persist.db.generated.tables.IncomeCategory.INCOME_CATEGORY
import moneytree.persist.db.generated.tables.daos.IncomeDao
import moneytree.persist.db.generated.tables.pojos.Income
import org.jooq.Record

class IncomeRepository(
    private val incomeDao: IncomeDao
) : Repository<IncomeDomain>, SummaryRepository<IncomeSummary> {

    private fun Record.toDomain(): IncomeDomain {
        return IncomeDomain(
            id = this[INCOME.ID],
            source = this[INCOME.SOURCE],
            incomeCategory = this[INCOME.INCOME_CATEGORY],
            transactionDate = this[INCOME.TRANSACTION_DATE],
            transactionAmount = this[INCOME.TRANSACTION_AMOUNT],
            notes = this[INCOME.NOTES],
            hide = this[INCOME.HIDE]
        )
    }

    private fun Income.toDomain(): IncomeDomain {
        return IncomeDomain(
            id = this.id,
            source = this.source,
            incomeCategory = this.incomeCategory,
            transactionDate = this.transactionDate,
            transactionAmount = this.transactionAmount,
            notes = this.notes,
            hide = this.hide
        )
    }

    private fun Record.toSummaryDomain(): IncomeSummary {
        return IncomeSummary(
            id = this[INCOME.ID],
            source = this[INCOME.SOURCE],
            incomeCategoryName = this[INCOME_CATEGORY.NAME],
            transactionDate = this[INCOME.TRANSACTION_DATE],
            transactionAmount = this[INCOME.TRANSACTION_AMOUNT],
            notes = this[INCOME.NOTES],
            hide = this[INCOME.HIDE]
        )
    }

    override fun get(): Result<List<IncomeDomain>, Throwable> {
        return resultTry {
            val result = incomeDao.configuration().dsl()
                .select()
                .from(INCOME)
                .limit(100)
                .fetch()

            result.mapNotNull { it.toDomain() }
        }
    }

    override fun getById(uuid: UUID): Result<IncomeDomain?, Throwable> {
        return resultTry {
            incomeDao.fetchOneById(uuid)?.toDomain()
        }
    }

    override fun insert(newEntity: IncomeDomain): Result<IncomeDomain, Throwable> {
        return resultTry {
            val result = incomeDao.configuration().dsl()
                .insertInto(INCOME)
                .columns(
                    INCOME.ID,
                    INCOME.SOURCE,
                    INCOME.INCOME_CATEGORY,
                    INCOME.TRANSACTION_DATE,
                    INCOME.TRANSACTION_AMOUNT,
                    INCOME.NOTES,
                    INCOME.HIDE
                )
                .values(
                    newEntity.id ?: UUID.randomUUID(),
                    newEntity.source,
                    newEntity.incomeCategory,
                    newEntity.transactionDate,
                    newEntity.transactionAmount,
                    newEntity.notes,
                    newEntity.hide
                )
                .returning()
                .fetch()

            result.first().toDomain()
        }
    }

    override fun upsertById(
        updatedEntity: IncomeDomain,
        uuid: UUID
    ): Result<IncomeDomain, Throwable> {
        return resultTry {
            incomeDao.configuration().dsl()
                .insertInto(INCOME)
                .columns(
                    INCOME.ID,
                    INCOME.SOURCE,
                    INCOME.INCOME_CATEGORY,
                    INCOME.TRANSACTION_DATE,
                    INCOME.TRANSACTION_AMOUNT,
                    INCOME.NOTES,
                    INCOME.HIDE
                )
                .values(
                    uuid,
                    updatedEntity.source,
                    updatedEntity.incomeCategory,
                    updatedEntity.transactionDate,
                    updatedEntity.transactionAmount,
                    updatedEntity.notes,
                    updatedEntity.hide
                )
                .onDuplicateKeyUpdate()
                .set(INCOME.SOURCE, updatedEntity.source)
                .set(INCOME.INCOME_CATEGORY, updatedEntity.incomeCategory)
                .set(INCOME.TRANSACTION_DATE, updatedEntity.transactionDate)
                .set(INCOME.TRANSACTION_AMOUNT, updatedEntity.transactionAmount)
                .set(INCOME.NOTES, updatedEntity.notes)
                .set(INCOME.HIDE, updatedEntity.hide)
                .execute()

            updatedEntity.copy(id = uuid)
        }
    }

    override fun getSummary(): Result<List<IncomeSummary>, Throwable> {
        return resultTry {
            val result = incomeDao.configuration().dsl()
                .select()
                .from(INCOME)
                .join(INCOME_CATEGORY).on(INCOME.INCOME_CATEGORY.eq(INCOME_CATEGORY.ID))
                .limit(100)
                .fetch()

            result.mapNotNull { it.toSummaryDomain() }
        }
    }

    override fun getSummaryById(uuid: UUID): Result<IncomeSummary?, Throwable> {
        return resultTry {
            val result = incomeDao.configuration().dsl()
                .select()
                .from(INCOME)
                .join(INCOME_CATEGORY).on(INCOME.INCOME_CATEGORY.eq(INCOME_CATEGORY.ID))
                .where(INCOME.ID.eq(uuid))
                .fetch()

            if (result.isNotEmpty)
                result.first().toSummaryDomain()
            else
                null
        }
    }
}
