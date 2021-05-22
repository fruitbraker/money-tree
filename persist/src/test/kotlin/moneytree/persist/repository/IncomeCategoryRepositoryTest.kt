package moneytree.persist.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.domain.entity.IncomeCategory
import moneytree.libs.commons.result.onOk
import moneytree.libs.commons.result.shouldBeOk
import moneytree.libs.commons.result.toOkValue
import moneytree.libs.testcommons.randomString
import moneytree.persist.PersistConnectorTestHarness
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IncomeCategoryRepositoryTest : PersistConnectorTestHarness() {

    @AfterAll
    fun clean() {
        super.close()
    }

    @Test
    fun `get with unknown UUID returns null`() {
        val getResult = incomeCategoryRepository.getById(UUID.randomUUID())

        getResult.shouldBeOk()
        getResult.onOk { it shouldBe null }
    }

    @Test
    fun `insert and getById happy path`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()

        val retrieveResult = incomeCategoryRepository.getById(
            checkNotNull(randomIncomeCategory.id)
        )
        retrieveResult.shouldBeOk()
        retrieveResult.onOk {
            it shouldBe randomIncomeCategory
        }
    }

    @Test
    fun `generic get returns a list`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()

        val getResult = incomeCategoryRepository.get()
        getResult.shouldBeOk()
        getResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain randomIncomeCategory
        }
    }

    @Test
    fun `upsert updates existing entity`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()
        val randomIncomeCategoryId = checkNotNull(randomIncomeCategory.id)

        val newRandomString = randomString()

        val updatedIncomeCategory = IncomeCategory(
            id = randomIncomeCategoryId,
            name = newRandomString
        )

        val upsertResult = incomeCategoryRepository.upsertById(updatedIncomeCategory, randomIncomeCategoryId)
        upsertResult.shouldBeOk()
        upsertResult.onOk {
            it shouldBe updatedIncomeCategory
        }

        val retrieveResult = incomeCategoryRepository.getById(randomIncomeCategoryId)
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

    @Test
    fun `deleteById successfully deletes`() {
        val randomIncomeCategory = insertRandomIncomeCategory().toOkValue()
        val randomIncomeCategoryId = checkNotNull(randomIncomeCategory.id)


        val deleteResult = incomeCategoryRepository.deleteById(randomIncomeCategoryId)
        deleteResult.shouldBeOk()

        val nullResult = incomeCategoryRepository.getById(randomIncomeCategoryId)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `deleteById does not error on nonexistent uuid`() {
        val randomUUID = UUID.randomUUID()

        val deleteResult = incomeCategoryRepository.deleteById(randomUUID)
        deleteResult.shouldBeOk()
    }
}
