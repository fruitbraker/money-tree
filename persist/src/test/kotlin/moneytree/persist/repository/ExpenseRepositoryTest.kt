package moneytree.persist.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.Expense
import moneytree.domain.entity.ExpenseSummary
import moneytree.domain.entity.ExpenseSummaryFilter
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
class ExpenseRepositoryTest : PersistConnectorTestHarness() {

    @AfterAll
    fun clean() {
        super.close()
    }

    @Test
    fun `get with unknown UUID returns null`() {
        val result = expenseRepository.getById(UUID.randomUUID())

        result.shouldBeOk()
        result.onOk { it shouldBe null }
    }

    @Test
    fun `insert and getById happy path`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val randomExpense = insertRandomExpense(
            checkNotNull(randomVendor.vendorId),
            checkNotNull(randomExpenseCategory.expenseCategoryId)
        ).toOkValue()

        val getResult = expenseRepository.getById(checkNotNull(randomExpense.id))
        getResult.shouldBeOk()
        getResult.onOk { it shouldBe randomExpense }
    }

    @Test
    fun `insert unknown vendor violates foreign key constraint`() {
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()
        val vendorId = UUID.randomUUID()

        val insertResult = insertRandomExpense(
            vendorId,
            checkNotNull(randomExpenseCategory.expenseCategoryId)
        )

        insertResult.shouldBeErr()
        insertResult.onErr { it.localizedMessage shouldContain FOREIGN_KEY_CONSTRAINT_VIOLATION }
    }

    @Test
    fun `insert unknown expense category violates foreign key constraint`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val expenseCategoryId = UUID.randomUUID()

        val insertResult = insertRandomExpense(
            checkNotNull(randomVendor.vendorId),
            expenseCategoryId
        )

        insertResult.shouldBeErr()
        insertResult.onErr { it.localizedMessage shouldContain FOREIGN_KEY_CONSTRAINT_VIOLATION }
    }

    @Test
    fun `generic get returns list of expense`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val randomExpense = insertRandomExpense(
            checkNotNull(randomVendor.vendorId),
            checkNotNull(randomExpenseCategory.expenseCategoryId)
        ).toOkValue()

        val getResult = expenseRepository.get()
        getResult.shouldBeOk()
        getResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain randomExpense
        }
    }

    @Test
    fun `upsertById updates existing entity`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val randomExpense = insertRandomExpense(
            checkNotNull(randomVendor.vendorId),
            checkNotNull(randomExpenseCategory.expenseCategoryId)
        ).toOkValue()

        val newRandomVendor = insertRandomVendor().toOkValue()
        val newRandomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val updatedExpense = randomExpense.copy(
            vendorId = checkNotNull(newRandomVendor.vendorId),
            expenseCategoryId = checkNotNull(newRandomExpenseCategory.expenseCategoryId)
        )

        val upsertResult = expenseRepository.upsertById(updatedExpense, checkNotNull(randomExpense.id))
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe updatedExpense }
    }

    @Test
    fun `upsertById adds a new entity`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = checkNotNull(randomVendor.vendorId)
        val expenseCategoryId = checkNotNull(randomExpenseCategory.expenseCategoryId)
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendorId = vendorId,
            expenseCategoryId = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val nullResult = expenseRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }

        val upsertResult = expenseRepository.upsertById(expense, randomUUID)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe expense }

        val getResult = expenseRepository.getById(randomUUID)
        getResult.shouldBeOk()
        getResult.onOk { it shouldBe expense }
    }

    @Test
    fun `upsertById upserts with id parameter`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val parameterId = UUID.randomUUID()
        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = checkNotNull(randomVendor.vendorId)
        val expenseCategoryId = checkNotNull(randomExpenseCategory.expenseCategoryId)
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendorId = vendorId,
            expenseCategoryId = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val idParameterExpense = expense.copy(id = parameterId)

        val upsertResult = expenseRepository.upsertById(expense, parameterId)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe idParameterExpense }

        val getResult = expenseRepository.getById(parameterId)
        getResult.shouldBeOk()
        getResult.onOk { it shouldBe idParameterExpense }

        val nullResult = expenseRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `upsertById fails foreign key constraint`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val expense = insertRandomExpense(
            checkNotNull(randomVendor.vendorId),
            checkNotNull(randomExpenseCategory.expenseCategoryId)
        ).toOkValue()

        val updatedExpense = expense.copy(vendorId = UUID.randomUUID(), expenseCategoryId = UUID.randomUUID())

        val upsertResult = expenseRepository.upsertById(
            updatedExpense,
            checkNotNull(expense.id)
        )
        upsertResult.shouldBeErr()
        upsertResult.onErr { it.localizedMessage shouldContain FOREIGN_KEY_CONSTRAINT_VIOLATION }
    }

    @Test
    fun `getSummary happy path with valid filter`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val randomExpense = insertRandomExpense(
            checkNotNull(randomVendor.vendorId),
            checkNotNull(randomExpenseCategory.expenseCategoryId)
        ).toOkValue()

        val expenseSummary = ExpenseSummary(
            id = checkNotNull(randomExpense.id),
            transactionDate = randomExpense.transactionDate,
            transactionAmount = randomExpense.transactionAmount,
            vendorId = randomExpense.vendorId,
            vendorName = randomVendor.name,
            expenseCategoryId = randomExpense.expenseCategoryId,
            expenseCategoryName = randomExpenseCategory.name,
            notes = randomExpense.notes,
            hide = randomExpense.hide
        )

        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now(),
            vendorIds = listOf(randomExpense.vendorId),
            expenseCategoryIds = listOf(randomExpense.expenseCategoryId)
        )

        val summaryResult = expenseRepository.getSummary(expenseSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain expenseSummary
        }
    }

    @Test
    fun `getSummary returns empty list when filter doesn't meet date range`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val randomExpense = insertRandomExpense(
            checkNotNull(randomVendor.vendorId),
            checkNotNull(randomExpenseCategory.expenseCategoryId)
        ).toOkValue()

        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = randomExpense.transactionDate.plusDays(1),
            endDate = randomExpense.transactionDate.plusDays(2),
            vendorIds = emptyList(),
            expenseCategoryIds = emptyList()
        )

        val summaryResult = expenseRepository.getSummary(expenseSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it shouldBe emptyList()
        }
    }

    @Test
    fun `getSummary returns empty list when filter doesn't contain matching vendorId`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val randomExpense = insertRandomExpense(
            checkNotNull(randomVendor.vendorId),
            checkNotNull(randomExpenseCategory.expenseCategoryId)
        ).toOkValue()

        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = randomExpense.transactionDate.minusDays(1),
            endDate = randomExpense.transactionDate,
            vendorIds = listOf(UUID.randomUUID()),
            expenseCategoryIds = emptyList()
        )

        val summaryResult = expenseRepository.getSummary(expenseSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it shouldBe emptyList()
        }
    }

    @Test
    fun `getSummary returns empty list when filter doesn't contain expenseCategoryId`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val randomExpense = insertRandomExpense(
            checkNotNull(randomVendor.vendorId),
            checkNotNull(randomExpenseCategory.expenseCategoryId)
        ).toOkValue()

        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = randomExpense.transactionDate.minusDays(1),
            endDate = randomExpense.transactionDate,
            vendorIds = emptyList(),
            expenseCategoryIds = listOf(UUID.randomUUID())
        )

        val summaryResult = expenseRepository.getSummary(expenseSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it shouldBe emptyList()
        }
    }

    @Test
    fun `deleteById successfully deletes`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val randomExpense = insertRandomExpense(
            checkNotNull(randomVendor.vendorId),
            checkNotNull(randomExpenseCategory.expenseCategoryId)
        ).toOkValue()

        val randomExpenseId = checkNotNull(randomExpense.id)

        val deleteResult = expenseRepository.deleteById(randomExpenseId)
        deleteResult.shouldBeOk()

        val nullResult = expenseRepository.getById(randomExpenseId)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `deleteById does not error on nonexistent uuid`() {
        val randomUUID = UUID.randomUUID()

        val deleteResult = expenseRepository.deleteById(randomUUID)
        deleteResult.shouldBeOk()
    }
}
