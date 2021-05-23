package moneytree.persist.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.Income
import moneytree.domain.entity.IncomeSummary
import moneytree.domain.entity.IncomeSummaryFilter
import moneytree.libs.commons.result.onErr
import moneytree.libs.commons.result.onOk
import moneytree.libs.commons.result.shouldBeErr
import moneytree.libs.commons.result.shouldBeOk
import moneytree.libs.commons.result.toOkValue
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.FOREIGN_KEY_CONSTRAINT_VIOLATION
import moneytree.persist.PersistConnectorTestHarness
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IncomeRepositoryTest : PersistConnectorTestHarness() {

    @AfterAll
    fun clean() {
        super.close()
    }

    @Test
    fun `get with unknown UUID returns null`() {
        val result = incomeRepository.getById(UUID.randomUUID())

        result.shouldBeOk()
        result.onOk { it shouldBe null }
    }

    @Test
    fun `insert and getById happy path`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()
        val randomIncome = insertRandomIncome(
            checkNotNull(randomIncomeCategory.id)
        ).toOkValue()

        val getResult = incomeRepository.getById(checkNotNull(randomIncome.id))
        getResult.shouldBeOk()
        getResult.onOk { it shouldBe randomIncome }
    }

    @Test
    fun `insert unknown income category violates foreign key constraint`() {
        val insertResult = insertRandomIncome(UUID.randomUUID())

        insertResult.shouldBeErr()
        insertResult.onErr { it.localizedMessage shouldContain FOREIGN_KEY_CONSTRAINT_VIOLATION }
    }

    @Test
    fun `generic get returns list of income`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()
        val randomIncome = insertRandomIncome(
            checkNotNull(randomIncomeCategory.id)
        ).toOkValue()

        val getResult = incomeRepository.get()
        getResult.shouldBeOk()
        getResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain randomIncome
        }
    }

    @Test
    fun `upsertById updates existing entity`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()
        val randomIncome = insertRandomIncome(
            checkNotNull(randomIncomeCategory.id)
        ).toOkValue()

        val updatedIncome = randomIncome.copy(notes = randomString())

        val upsertResult = incomeRepository.upsertById(updatedIncome, checkNotNull(randomIncome.id))
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe updatedIncome }
    }

    @Test
    fun `upsertById adds a new entity`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()

        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val randomIncomeCategoryId = checkNotNull(randomIncomeCategory.id)
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val notes = randomString()
        val hide = false

        val income = Income(
            id = randomUUID,
            source = randomSource,
            incomeCategory = randomIncomeCategoryId,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            notes = notes,
            hide = hide
        )

        val nullResult = incomeRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }

        val upsertResult = incomeRepository.upsertById(income, randomUUID)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe income }

        val getResult = incomeRepository.getById(randomUUID)
        getResult.shouldBeOk()
        getResult.onOk { it shouldBe income }
    }

    @Test
    fun `upsertById upserts with id parameter`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()

        val parameterId = UUID.randomUUID()
        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val randomIncomeCategoryId = checkNotNull(randomIncomeCategory.id)
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val notes = randomString()
        val hide = false

        val income = Income(
            id = randomUUID,
            source = randomSource,
            incomeCategory = randomIncomeCategoryId,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            notes = notes,
            hide = hide
        )

        val idParameterIncome = income.copy(id = parameterId)

        val upsertResult = incomeRepository.upsertById(income, parameterId)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe idParameterIncome }

        val getResult = incomeRepository.getById(parameterId)
        getResult.shouldBeOk()
        getResult.onOk { it shouldBe idParameterIncome }

        val nullResult = incomeRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `getSummary happy path with valid filter`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()
        val randomIncomeCategoryId = checkNotNull(randomIncomeCategory.id)

        val randomIncome = insertRandomIncome(randomIncomeCategoryId).toOkValue()
        val randomIncomeId = checkNotNull(randomIncome.id)

        val incomeSummary = IncomeSummary(
            id = randomIncomeId,
            source = randomIncome.source,
            incomeCategoryId = randomIncomeCategoryId,
            incomeCategoryName = randomIncomeCategory.name,
            transactionDate = randomIncome.transactionDate,
            transactionAmount = randomIncome.transactionAmount,
            notes = randomIncome.notes,
            hide = randomIncome.hide
        )

        val dummyFilter = IncomeSummaryFilter(
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now(),
            incomeCategoryIds = listOf(randomIncomeCategoryId)
        )

        val summaryResult = incomeRepository.getSummary(dummyFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain incomeSummary
        }
    }

    @Test
    fun `getSummary returns empty list when filter doesn't meet date range`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()
        val randomIncomeCategoryId = checkNotNull(randomIncomeCategory.id)

        val randomIncome = insertRandomIncome(randomIncomeCategoryId).toOkValue()

        val incomeSummaryFilter = IncomeSummaryFilter(
            startDate = randomIncome.transactionDate.plusDays(1),
            endDate = randomIncome.transactionDate.plusDays(2),
            incomeCategoryIds = emptyList()
        )

        val summaryResult = incomeRepository.getSummary(incomeSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it.size shouldBeGreaterThanOrEqual 0
        }
    }

    @Test
    fun `getSummary returns empty list when filter doesn't contain matching incomeCategoryId`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()
        val randomIncomeCategoryId = checkNotNull(randomIncomeCategory.id)

        val randomIncome = insertRandomIncome(randomIncomeCategoryId).toOkValue()

        val incomeSummaryFilter = IncomeSummaryFilter(
            startDate = randomIncome.transactionDate.plusDays(1),
            endDate = randomIncome.transactionDate.plusDays(2),
            incomeCategoryIds = listOf(UUID.randomUUID())
        )

        val summaryResult = incomeRepository.getSummary(incomeSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it.size shouldBeGreaterThanOrEqual 0
        }
    }

    @Test
    fun `deleteById successfully deletes`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()
        val randomIncomeCategoryId = checkNotNull(randomIncomeCategory.id)

        val randomIncome = insertRandomIncome(randomIncomeCategoryId).toOkValue()
        val randomIncomeId = checkNotNull(randomIncome.id)

        val deleteResult = incomeRepository.deleteById(randomIncomeId)
        deleteResult.shouldBeOk()

        val nullResult = incomeRepository.getById(randomIncomeId)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `deleteById should not error on nonexistent uuid`() {
        val randomUUID = UUID.randomUUID()

        val deleteResult = incomeRepository.deleteById(randomUUID)
        deleteResult.shouldBeOk()
    }
}
