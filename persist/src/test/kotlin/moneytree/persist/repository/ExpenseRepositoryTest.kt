package moneytree.persist.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.Expense
import moneytree.domain.entity.ExpenseCategory
import moneytree.domain.entity.ExpenseSummary
import moneytree.domain.entity.ExpenseSummaryFilter
import moneytree.domain.entity.Vendor
import moneytree.libs.commons.result.onErr
import moneytree.libs.commons.result.onOk
import moneytree.libs.commons.result.shouldBeErr
import moneytree.libs.commons.result.shouldBeOk
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.FOREIGN_KEY_CONSTRAINT_VIOLATION
import moneytree.persist.PersistConnectorTestHarness
import moneytree.persist.db.generated.tables.daos.ExpenseCategoryDao
import moneytree.persist.db.generated.tables.daos.ExpenseDao
import moneytree.persist.db.generated.tables.daos.VendorDao
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseRepositoryTest {

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
        val result = expenseRepository.getById(UUID.randomUUID())

        result.shouldBeOk()
        result.onOk { it shouldBe null }
    }

    @Test
    fun `insert and getById happy path`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()
        insertResult.onOk { it shouldBe expense }

        val getResult = expenseRepository.getById(randomUUID)
        getResult.shouldBeOk()
        getResult.onOk { it shouldBe expense }
    }

    @Test
    fun `insert unknown vendor violates foreign key constraint`() {
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = UUID.randomUUID()
        val expenseCategoryId = randomExpenseCategory.id ?: throw IllegalStateException("Expense category cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val insertResult = expenseRepository.insert(expense)

        insertResult.shouldBeErr()
        insertResult.onErr { it.localizedMessage shouldContain FOREIGN_KEY_CONSTRAINT_VIOLATION }
    }

    @Test
    fun `insert unknown expense category violates foreign key constraint`() {
        val randomVendor = insertRandomVendor()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = UUID.randomUUID()
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val insertResult = expenseRepository.insert(expense)

        insertResult.shouldBeErr()
        insertResult.onErr {
            it.localizedMessage.shouldContain(FOREIGN_KEY_CONSTRAINT_VIOLATION)
        }
    }

    @Test
    fun `generic get returns list of expense`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()

        val getResult = expenseRepository.get()
        getResult.shouldBeOk()
        getResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain expense
        }
    }

    @Test
    fun `upsertById updates existing entity`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()

        val newRandomVendor = insertRandomVendor()
        val newRandomExpenseCategory = insertRandomExpenseCategory()

        val updatedExpense = expense.copy(
            vendor = newRandomVendor.id ?: fail("Vendor id cannot be null!"),
            expenseCategory = newRandomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        )

        val upsertResult = expenseRepository.upsertById(updatedExpense, randomUUID)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe updatedExpense }
    }

    @Test
    fun `upsertById adds a new entity`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
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
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val parameterId = UUID.randomUUID()
        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
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
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()

        val updatedExpense = expense.copy(vendor = UUID.randomUUID(), expenseCategory = UUID.randomUUID())

        val upsertResult = expenseRepository.upsertById(updatedExpense, randomUUID)
        upsertResult.shouldBeErr()
        upsertResult.onErr { it.localizedMessage shouldContain FOREIGN_KEY_CONSTRAINT_VIOLATION }
    }

    @Test
    fun `getSummary happy path with null filter`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val expenseSummary = ExpenseSummary(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendorId = vendorId,
            vendorName = randomVendor.name,
            expenseCategoryId = expenseCategoryId,
            expenseCategoryName = randomExpenseCategory.name,
            notes = notes,
            hide = hide
        )

        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = null,
            endDate = null,
            vendorIds = null,
            expenseCategoryIds = null
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()

        val summaryResult = expenseRepository.getSummary(expenseSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain expenseSummary
        }
    }

    @Test
    fun `getSummary happy path with valid filter`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val expenseSummary = ExpenseSummary(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendorId = vendorId,
            vendorName = randomVendor.name,
            expenseCategoryId = expenseCategoryId,
            expenseCategoryName = randomExpenseCategory.name,
            notes = notes,
            hide = hide
        )

        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now(),
            vendorIds = listOf(expense.vendor),
            expenseCategoryIds = listOf(expense.expenseCategory)
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()

        val summaryResult = expenseRepository.getSummary(expenseSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain expenseSummary
        }
    }

    @Test
    fun `getSummary returns empty list when filter doesn't meet date range`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = todayLocalDate.plusDays(1),
            endDate = todayLocalDate.plusDays(2),
            vendorIds = null,
            expenseCategoryIds = null
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()

        val summaryResult = expenseRepository.getSummary(expenseSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it shouldBe emptyList()
        }
    }

    @Test
    fun `getSummary returns empty list when filter doesn't contain matching vendorId`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = null,
            endDate = null,
            vendorIds = listOf(UUID.randomUUID()),
            expenseCategoryIds = null
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()

        val summaryResult = expenseRepository.getSummary(expenseSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it shouldBe emptyList()
        }
    }

    @Test
    fun `getSummary returns empty list when filter doesn't contain expenseCategoryId`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val expenseSummaryFilter = ExpenseSummaryFilter(
            startDate = null,
            endDate = null,
            vendorIds = null,
            expenseCategoryIds = listOf(UUID.randomUUID())
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()

        val summaryResult = expenseRepository.getSummary(expenseSummaryFilter)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it shouldBe emptyList()
        }
    }

    @Test
    fun `getSummaryById happy path`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val expenseSummary = ExpenseSummary(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendorId = vendorId,
            vendorName = randomVendor.name,
            expenseCategoryId = expenseCategoryId,
            expenseCategoryName = randomExpenseCategory.name,
            notes = notes,
            hide = hide
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()

        val summaryResult = expenseRepository.getSummaryById(randomUUID)
        summaryResult.shouldBeOk()
        summaryResult.onOk {
            it shouldBe expenseSummary
        }
    }

    @Test
    fun `getSummaryById returns null on unknown id`() {
        val randomUUID = UUID.randomUUID()

        val nullResult = expenseRepository.getSummaryById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `deleteById successfully deletes`() {
        val randomVendor = insertRandomVendor()
        val randomExpenseCategory = insertRandomExpenseCategory()

        val randomUUID = UUID.randomUUID()
        val todayLocalDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val vendorId = randomVendor.id ?: fail("Vendor id cannot be null!")
        val expenseCategoryId = randomExpenseCategory.id ?: fail("Expense category id cannot be null!")
        val notes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayLocalDate,
            transactionAmount = randomTransactionAmount,
            vendor = vendorId,
            expenseCategory = expenseCategoryId,
            notes = notes,
            hide = hide
        )

        val insertResult = expenseRepository.insert(expense)
        insertResult.shouldBeOk()

        val deleteResult = expenseRepository.deleteById(randomUUID)
        deleteResult.shouldBeOk()

        val nullResult = expenseRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `deleteById does not error on nonexistent uuid`() {
        val randomUUID = UUID.randomUUID()

        val deleteResult = expenseRepository.deleteById(randomUUID)
        deleteResult.shouldBeOk()
    }

    private fun insertRandomVendor(): Vendor {
        val vendor = Vendor(
            id = UUID.randomUUID(),
            name = randomString()
        )

        val result = vendorRepository.insert(vendor)
        result.shouldBeOk()

        return vendor
    }

    private fun insertRandomExpenseCategory(): ExpenseCategory {
        val expenseCategory = ExpenseCategory(
            id = UUID.randomUUID(),
            name = randomString(),
            targetAmount = randomBigDecimal()
        )

        val result = expenseCategoryRepository.insert(expenseCategory)
        result.shouldBeOk()

        return expenseCategory
    }
}
