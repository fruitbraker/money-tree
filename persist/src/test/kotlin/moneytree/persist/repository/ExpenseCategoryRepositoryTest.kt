package moneytree.persist.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.ExpenseCategory
import moneytree.domain.entity.ExpenseCategoryFilter
import moneytree.domain.entity.ExpenseCategorySummary
import moneytree.libs.commons.result.onOk
import moneytree.libs.commons.result.shouldBeOk
import moneytree.libs.commons.result.toOkValue
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.PersistConnectorTestHarness
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseCategoryRepositoryTest : PersistConnectorTestHarness() {

    @AfterAll
    fun clean() {
        super.close()
    }

    @Test
    fun `get with unknown UUID returns null`() {
        val getResult = expenseCategoryRepository.getById(UUID.randomUUID())

        getResult.shouldBeOk()
        getResult.onOk { it shouldBe null }
    }

    @Test
    fun `insert and getById happy path`() {
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val getById = expenseCategoryRepository.getById(
            checkNotNull(randomExpenseCategory.id)
        )

        getById.shouldBeOk()
        getById.onOk {
            it shouldBe randomExpenseCategory
        }
    }

    @Test
    fun `generic get returns a list`() {
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()

        val getResult = expenseCategoryRepository.get()
        getResult.shouldBeOk()
        getResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain randomExpenseCategory
        }
    }

    @Test
    fun `upsert updates existing entity`() {
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()
        val randomExpenseCategoryId = checkNotNull(randomExpenseCategory.id)

        val newRandomString = randomString()
        val newRandomTargetAmount = randomBigDecimal()

        val updatedExpenseCategory = ExpenseCategory(
            id = randomExpenseCategoryId,
            name = newRandomString,
            targetAmount = newRandomTargetAmount
        )

        val upsertResult =
            expenseCategoryRepository.upsertById(updatedExpenseCategory, randomExpenseCategoryId)
        upsertResult.shouldBeOk()
        upsertResult.onOk {
            it shouldBe updatedExpenseCategory
        }

        val retrieveResult = expenseCategoryRepository.getById(randomExpenseCategoryId)
        retrieveResult.shouldBeOk()
        retrieveResult.onOk {
            it shouldBe updatedExpenseCategory
        }
    }

    @Test
    fun `upsert inserts new entity`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()
        val randomTargetAmount = randomBigDecimal()

        val expenseCategory = ExpenseCategory(
            id = randomUUID,
            name = randomString,
            targetAmount = randomTargetAmount
        )

        val nullResult = expenseCategoryRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }

        val upsertResult = expenseCategoryRepository.upsertById(expenseCategory, randomUUID)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe expenseCategory }

        val getResult = expenseCategoryRepository.getById(randomUUID)
        getResult.shouldBeOk()
        getResult.onOk {
            it shouldBe expenseCategory
        }
    }

    @Test
    fun `upsertById upserts with parameter id`() {
        val parameterId = UUID.randomUUID()
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()
        val randomTargetAmount = randomBigDecimal()

        val expenseCategory = ExpenseCategory(
            id = randomUUID,
            name = randomString,
            targetAmount = randomTargetAmount
        )

        val idParameterExpenseCategory = expenseCategory.copy(id = parameterId)

        val upsertResult = expenseCategoryRepository.upsertById(expenseCategory, parameterId)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe idParameterExpenseCategory }

        val nullResult = expenseCategoryRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `deleteById deletes existing successfully`() {
        val randomExpenseCategory = insertRandomExpenseCategory().toOkValue()
        val randomExpenseCategoryId = checkNotNull(randomExpenseCategory.id)

        val deleteResult = expenseCategoryRepository.deleteById(randomExpenseCategoryId)
        deleteResult.shouldBeOk()

        val nullResult = expenseCategoryRepository.getById(randomExpenseCategoryId)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `deleteById does not error on nonexistent uuid`() {
        val randomUUID = UUID.randomUUID()

        val deleteResult = expenseCategoryRepository.deleteById(randomUUID)
        deleteResult.shouldBeOk()
    }

    @Test
    fun `getSummary for expense category aggregates transaction amount on expense category ids happy path`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomVendorId = checkNotNull(randomVendor.id)

        val randomExpenseCategory1 = insertRandomExpenseCategory().toOkValue()
        val randomExpenseCategory1Id = checkNotNull(randomExpenseCategory1.id)
        val randomExpense1WithExpenseCategory1 = insertRandomExpense(
            randomVendorId,
            randomExpenseCategory1Id
        ).toOkValue()
        val randomExpense2WithExpenseCategory1 = insertRandomExpense(
            randomVendorId,
            randomExpenseCategory1Id
        ).toOkValue()

        val randomExpenseCategory2 = insertRandomExpenseCategory().toOkValue()
        val randomExpenseCategory2Id = checkNotNull(randomExpenseCategory2.id)
        val randomExpense1WithExpenseCategory2 = insertRandomExpense(
            randomVendorId,
            randomExpenseCategory2Id
        ).toOkValue()
        val randomExpense2WithExpenseCategory2 = insertRandomExpense(
            randomVendorId,
            randomExpenseCategory2Id
        ).toOkValue()

        val expectedExpenseCategorySummary = listOf(
            ExpenseCategorySummary(
                id = randomExpenseCategory1Id,
                name = randomExpenseCategory1.name,
                totalAmount = randomExpense1WithExpenseCategory1.transactionAmount + randomExpense2WithExpenseCategory1.transactionAmount
            ),
            ExpenseCategorySummary(
                id = randomExpenseCategory2Id,
                name = randomExpenseCategory2.name,
                totalAmount = randomExpense1WithExpenseCategory2.transactionAmount + randomExpense2WithExpenseCategory2.transactionAmount
            )
        )

        val filter = ExpenseCategoryFilter(
            ids = emptyList(),
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now()
        )

        val getSummaryResult = expenseCategoryRepository.getSummary(filter)
        getSummaryResult.shouldBeOk()
        getSummaryResult.onOk {
            it shouldBe expectedExpenseCategorySummary
        }
    }

    @Test
    fun `getSummary with filter on ids happy path`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomVendorId = checkNotNull(randomVendor.id)

        val randomExpenseCategory1 = insertRandomExpenseCategory().toOkValue()
        val randomExpenseCategory1Id = checkNotNull(randomExpenseCategory1.id)
        val randomExpense1WithExpenseCategory1 = insertRandomExpense(
            randomVendorId,
            randomExpenseCategory1Id
        ).toOkValue()
        val randomExpense2WithExpenseCategory1 = insertRandomExpense(
            randomVendorId,
            randomExpenseCategory1Id
        ).toOkValue()

        val randomExpenseCategory2 = insertRandomExpenseCategory().toOkValue()
        val randomExpenseCategory2Id = checkNotNull(randomExpenseCategory2.id)
        insertRandomExpense(
            randomVendorId,
            randomExpenseCategory2Id
        )

        val expectedExpenseCategorySummary = listOf(
            ExpenseCategorySummary(
                id = randomExpenseCategory1Id,
                name = randomExpenseCategory1.name,
                totalAmount = randomExpense1WithExpenseCategory1.transactionAmount + randomExpense2WithExpenseCategory1.transactionAmount
            )
        )

        val filter = ExpenseCategoryFilter(
            ids = listOf(checkNotNull(randomExpenseCategory1.id)),
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now()
        )

        val getSummaryResult = expenseCategoryRepository.getSummary(filter)
        getSummaryResult.shouldBeOk()
        getSummaryResult.onOk {
            it shouldBe expectedExpenseCategorySummary
        }
    }
}
