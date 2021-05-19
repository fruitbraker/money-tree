package moneytree.persist.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.domain.entity.ExpenseCategory
import moneytree.libs.commons.result.onOk
import moneytree.libs.commons.result.shouldBeOk
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.PersistConnectorTestHarness
import moneytree.persist.db.generated.tables.daos.ExpenseCategoryDao
import moneytree.persist.db.generated.tables.daos.ExpenseDao
import moneytree.persist.db.generated.tables.daos.VendorDao
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseCategoryRepositoryTest {

    private val persistHarness = PersistConnectorTestHarness()
    private val expenseRepository = ExpenseRepository(
        ExpenseDao(persistHarness.dslContext.configuration())
    )

    private val expenseCategoryRepository = ExpenseCategoryRepository(
        ExpenseCategoryDao(persistHarness.dslContext.configuration())
    )

    private val vendorRepository = VendorRepository(
        VendorDao(persistHarness.dslContext.configuration())
    )

    @AfterAll
    fun clean() {
        persistHarness.close()
    }

    @Test
    fun `get with unknown UUID returns null`() {
        val getResult = expenseCategoryRepository.getById(UUID.randomUUID())

        getResult.shouldBeOk()
        getResult.onOk { it shouldBe null }
    }

    @Test
    fun `insert and getById happy path`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()
        val randomTargetAmount = randomBigDecimal()

        val expenseCategory = ExpenseCategory(
            id = randomUUID,
            name = randomString,
            targetAmount = randomTargetAmount
        )

        val insertResult = expenseCategoryRepository.insert(expenseCategory)
        insertResult.shouldBeOk()
        insertResult.onOk {
            it shouldBe expenseCategory
        }

        val retrieveResult = expenseCategoryRepository.getById(randomUUID)
        retrieveResult.shouldBeOk()
        retrieveResult.onOk {
            it shouldBe expenseCategory
        }
    }

    @Test
    fun `generic get returns a list`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()
        val randomTargetAmount = randomBigDecimal()

        val expenseCategory = ExpenseCategory(
            id = randomUUID,
            name = randomString,
            targetAmount = randomTargetAmount
        )

        expenseCategoryRepository.insert(expenseCategory)

        val getResult = expenseCategoryRepository.get()
        getResult.shouldBeOk()
        getResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain expenseCategory
        }
    }

    @Test
    fun `upsert updates existing entity`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()
        val randomTargetAmount = randomBigDecimal()

        val expenseCategory = ExpenseCategory(
            id = randomUUID,
            name = randomString,
            targetAmount = randomTargetAmount
        )

        expenseCategoryRepository.insert(expenseCategory)

        val newRandomString = randomString()
        val newRandomTargetAmount = randomBigDecimal()

        val updatedExpenseCategory = ExpenseCategory(
            id = randomUUID,
            name = newRandomString,
            targetAmount = newRandomTargetAmount
        )

        val insertResult = expenseCategoryRepository.upsertById(updatedExpenseCategory, randomUUID)
        insertResult.shouldBeOk()
        insertResult.onOk {
            it shouldBe updatedExpenseCategory
        }

        val retrieveResult = expenseCategoryRepository.getById(randomUUID)
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
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()
        val randomTargetAmount = randomBigDecimal()

        val expenseCategory = ExpenseCategory(
            id = randomUUID,
            name = randomString,
            targetAmount = randomTargetAmount
        )

        val insertResult = expenseCategoryRepository.insert(expenseCategory)
        insertResult.shouldBeOk()
        insertResult.onOk {
            it shouldBe expenseCategory
        }
        insertResult.shouldBeOk()

        val deleteResult = expenseCategoryRepository.deleteById(randomUUID)
        deleteResult.shouldBeOk()

        val nullResult = expenseCategoryRepository.getById(randomUUID)
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
    fun `getSummary for expense category aggregates similar categeory ids on transaction amount`() {

    }
}
