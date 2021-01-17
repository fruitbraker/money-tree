package moneytree.persist.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.domain.entity.IncomeCategory
import moneytree.libs.commons.result.onOk
import moneytree.libs.commons.result.shouldBeOk
import moneytree.libs.test.commons.randomBigDecimal
import moneytree.libs.test.commons.randomString
import moneytree.persist.PersistConnectorTestHarness
import moneytree.persist.db.generated.tables.daos.IncomeCategoryDao
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IncomeCategoryRepositoryTest {

    private val persistHarness = PersistConnectorTestHarness()
    private val incomeCategoryRepository = IncomeCategoryRepository(
        IncomeCategoryDao(persistHarness.dslContext.configuration())
    )

    @AfterAll
    fun clean() {
        persistHarness.close()
    }

    @Test
    fun `get with unknown UUID returns null`() {
        val getResult = incomeCategoryRepository.getById(UUID.randomUUID())

        getResult.shouldBeOk()
        getResult.onOk { it shouldBe null }
    }

    @Test
    fun `insert and getById happy path`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()

        val incomeCategory = IncomeCategory(
            id = randomUUID,
            name = randomString
        )

        val insertResult = incomeCategoryRepository.insert(incomeCategory)
        insertResult.shouldBeOk()
        insertResult.onOk {
            it shouldBe incomeCategory
        }

        val retrieveResult = incomeCategoryRepository.getById(randomUUID)
        retrieveResult.shouldBeOk()
        retrieveResult.onOk {
            it shouldBe incomeCategory
        }
    }

    @Test
    fun `generic get returns a list`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()

        val incomeCategory = IncomeCategory(
            id = randomUUID,
            name = randomString
        )

        incomeCategoryRepository.insert(incomeCategory)

        val getResult = incomeCategoryRepository.get()
        getResult.shouldBeOk()
        getResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain incomeCategory
        }
    }

    @Test
    fun `upsert updates existing entity`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()
        val randomTargetAmount = randomBigDecimal()

        val incomeCategory = IncomeCategory(
            id = randomUUID,
            name = randomString
        )

        incomeCategoryRepository.insert(incomeCategory)

        val newRandomString = randomString()

        val updatedIncomeCategory = IncomeCategory(
            id = randomUUID,
            name = newRandomString
        )

        val insertResult = incomeCategoryRepository.upsertById(updatedIncomeCategory, randomUUID)
        insertResult.shouldBeOk()
        insertResult.onOk {
            it shouldBe updatedIncomeCategory
        }

        val retrieveResult = incomeCategoryRepository.getById(randomUUID)
        retrieveResult.shouldBeOk()
        retrieveResult.onOk {
            it shouldBe updatedIncomeCategory
        }
    }

    @Test
    fun `upsert inserts new entity`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()

        val incomeCategory = IncomeCategory(
            id = randomUUID,
            name = randomString
        )

        val nullResult = incomeCategoryRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }

        val upsertResult = incomeCategoryRepository.upsertById(incomeCategory, randomUUID)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe incomeCategory }

        val getResult = incomeCategoryRepository.getById(randomUUID)
        getResult.shouldBeOk()
        getResult.onOk {
            it shouldBe incomeCategory
        }
    }

    @Test
    fun `upsertById upserts with parameter id`() {
        val parameterId = UUID.randomUUID()
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()

        val incomeCategory = IncomeCategory(
            id = randomUUID,
            name = randomString
        )

        val idParameterIncomeCategory = incomeCategory.copy(id = parameterId)

        val upsertResult = incomeCategoryRepository.upsertById(incomeCategory, parameterId)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe idParameterIncomeCategory }

        val nullResult = incomeCategoryRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }
}
