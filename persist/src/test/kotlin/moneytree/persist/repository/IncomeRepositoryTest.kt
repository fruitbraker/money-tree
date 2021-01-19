package moneytree.persist.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.Income
import moneytree.domain.entity.IncomeCategory
import moneytree.libs.commons.result.onErr
import moneytree.libs.commons.result.onOk
import moneytree.libs.commons.result.shouldBeErr
import moneytree.libs.commons.result.shouldBeOk
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.FOREIGN_KEY_CONSTRAINT_VIOLATION
import moneytree.persist.PersistConnectorTestHarness
import moneytree.persist.db.generated.tables.daos.IncomeCategoryDao
import moneytree.persist.db.generated.tables.daos.IncomeDao
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IncomeRepositoryTest {

    private val persistHarness = PersistConnectorTestHarness()
    private val incomeCategoryRepository = IncomeCategoryRepository(
        IncomeCategoryDao(persistHarness.dslContext.configuration())
    )
    private val incomeRepository = IncomeRepository(
        IncomeDao(persistHarness.dslContext.configuration())
    )

    @AfterAll
    fun clean() {
        persistHarness.close()
    }

    @Test
    fun `get with unknown UUID returns null`() {
        val result = incomeRepository.getById(UUID.randomUUID())

        result.shouldBeOk()
        result.onOk { it shouldBe null }
    }

    @Test
    fun `insert and getById happy path`() {
        val randomIncomeCategory = insertRandomIncomeCategory()

        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val randomIncomeCategoryId = randomIncomeCategory.id ?: fail("Expense category id cannot be null!")
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

        val insertResult = incomeRepository.insert(income)
        insertResult.shouldBeOk()
        insertResult.onOk { it shouldBe income }

        val getResult = incomeRepository.getById(randomUUID)
        getResult.shouldBeOk()
        getResult.onOk { it shouldBe income }
    }

    @Test
    fun `insert unknown income category violates foreign key constraint`() {
        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val randomIncomeCategoryId = UUID.randomUUID()
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

        val insertResult = incomeRepository.insert(income)

        insertResult.shouldBeErr()
        insertResult.onErr { it.localizedMessage shouldContain FOREIGN_KEY_CONSTRAINT_VIOLATION }
    }

    @Test
    fun `generic get returns list of income`() {
        val randomIncomeCategory = insertRandomIncomeCategory()

        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val randomIncomeCategoryId = randomIncomeCategory.id ?: fail("Expense category id cannot be null!")
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

        val insertResult = incomeRepository.insert(income)
        insertResult.shouldBeOk()

        val getResult = incomeRepository.get()
        getResult.shouldBeOk()
        getResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain income
        }
    }

    @Test
    fun `upsertById updates existing entity`() {
        val randomIncomeCategory = insertRandomIncomeCategory()

        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val randomIncomeCategoryId = randomIncomeCategory.id ?: fail("Expense category id cannot be null!")
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

        val insertResult = incomeRepository.insert(income)
        insertResult.shouldBeOk()

        val updatedIncome = income.copy(notes = randomString())

        val upsertResult = incomeRepository.upsertById(updatedIncome, randomUUID)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe updatedIncome }
    }

    @Test
    fun `upsertById adds a new entity`() {
        val randomIncomeCategory = insertRandomIncomeCategory()

        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val randomIncomeCategoryId = randomIncomeCategory.id ?: fail("Expense category id cannot be null!")
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
        val randomIncomeCategory = insertRandomIncomeCategory()

        val parameterId = UUID.randomUUID()
        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val randomIncomeCategoryId = randomIncomeCategory.id ?: fail("Expense category id cannot be null!")
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

    private fun insertRandomIncomeCategory(): IncomeCategory {
        val incomeCategory = IncomeCategory(
            id = UUID.randomUUID(),
            name = randomString()
        )

        val result = incomeCategoryRepository.insert(incomeCategory)
        result.shouldBeOk()

        return incomeCategory
    }
}
