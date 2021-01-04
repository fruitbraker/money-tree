package moneytree.persist

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.domain.ExpenseCategory
import moneytree.libs.commons.result.onOk
import moneytree.libs.commons.result.shouldBeOk
import moneytree.libs.test.commons.randomBigDecimal
import moneytree.libs.test.commons.randomString
import moneytree.persist.generated.tables.daos.ExpenseCategoryDao
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseCategoryRepositoryTest {

    private var persistHarness = PersistConnectorTestHarness()
    private var expenseCategoryRepository = ExpenseCategoryRepository(
        ExpenseCategoryDao(persistHarness.dslContext.configuration())
    )

    @AfterAll
    fun clean() {
        persistHarness.close()
    }

    @Test
    fun `get with unknown UUID returns null`() {
        val result = expenseCategoryRepository.getById(UUID.randomUUID())

        result.shouldBeOk()
        result.onOk { it shouldBe null }
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

        val result = expenseCategoryRepository.get()
        result.shouldBeOk()
        result.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain expenseCategory
        }
    }
}
