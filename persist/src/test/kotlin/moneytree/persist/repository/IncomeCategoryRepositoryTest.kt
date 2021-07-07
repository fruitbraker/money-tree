package moneytree.persist.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.IncomeCategory
import moneytree.domain.entity.IncomeCategoryFilter
import moneytree.domain.entity.IncomeCategorySummary
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
            checkNotNull(randomIncomeCategory.incomeCategoryId)
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
        val randomIncomeCategoryId = checkNotNull(randomIncomeCategory.incomeCategoryId)

        val newRandomString = randomString()

        val updatedIncomeCategory = IncomeCategory(
            incomeCategoryId = randomIncomeCategoryId,
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
            incomeCategoryId = randomUUID,
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
            incomeCategoryId = randomUUID,
            name = randomString
        )

        val idParameterIncomeCategory = incomeCategory.copy(incomeCategoryId = parameterId)

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
        val randomIncomeCategoryId = checkNotNull(randomIncomeCategory.incomeCategoryId)

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

    @Test
    fun `getSummary for IncomeCategory aggregates transaction amount on incomeCategoryId`() {
        val randomIncomeCategory1 = insertRandomIncomeCategory().toOkValue()
        val randomIncomeCategoryId1 = checkNotNull(randomIncomeCategory1.incomeCategoryId)

        val randomIncomeCategory2 = insertRandomIncomeCategory().toOkValue()
        val randomIncomeCategoryId2 = checkNotNull(randomIncomeCategory2.incomeCategoryId)

        val randomIncome1WithIncomeCategory1 = insertRandomIncome(randomIncomeCategoryId1).toOkValue()
        val randomIncome2WithIncomeCategory1 = insertRandomIncome(randomIncomeCategoryId1).toOkValue()

        val randomIncome1WithIncomeCategory2 = insertRandomIncome(randomIncomeCategoryId2).toOkValue()
        val randomIncome2WithIncomeCategory2 = insertRandomIncome(randomIncomeCategoryId2).toOkValue()

        val expectedIncomeCategorySummary = listOf(
            IncomeCategorySummary(
                id = randomIncomeCategoryId1,
                name = randomIncomeCategory1.name,
                totalAmount = randomIncome1WithIncomeCategory1.transactionAmount + randomIncome2WithIncomeCategory1.transactionAmount
            ),
            IncomeCategorySummary(
                id = randomIncomeCategoryId2,
                name = randomIncomeCategory2.name,
                totalAmount = randomIncome1WithIncomeCategory2.transactionAmount + randomIncome2WithIncomeCategory2.transactionAmount
            )
        )

        val filter = IncomeCategoryFilter(
            ids = emptyList(),
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now()
        )

        val getSummaryResult = incomeCategoryRepository.getSummary(filter)
        getSummaryResult.shouldBeOk()
        getSummaryResult.onOk {
            it shouldContainAll expectedIncomeCategorySummary
        }
    }

    @Test
    fun `getSummary with filter on ids happy path`() {
        val randomIncomeCategory1 = insertRandomIncomeCategory().toOkValue()
        val randomIncomeCategoryId1 = checkNotNull(randomIncomeCategory1.incomeCategoryId)

        val randomIncomeCategory2 = insertRandomIncomeCategory().toOkValue()
        val randomIncomeCategoryId2 = checkNotNull(randomIncomeCategory2.incomeCategoryId)

        val randomIncome1WithIncomeCategory1 = insertRandomIncome(randomIncomeCategoryId1).toOkValue()
        val randomIncome2WithIncomeCategory1 = insertRandomIncome(randomIncomeCategoryId1).toOkValue()

        insertRandomIncome(randomIncomeCategoryId2).toOkValue()
        insertRandomIncome(randomIncomeCategoryId2).toOkValue()

        val expectedIncomeCategorySummary = listOf(
            IncomeCategorySummary(
                id = randomIncomeCategoryId1,
                name = randomIncomeCategory1.name,
                totalAmount = randomIncome1WithIncomeCategory1.transactionAmount + randomIncome2WithIncomeCategory1.transactionAmount
            )
        )

        val filter = IncomeCategoryFilter(
            ids = listOf(randomIncomeCategoryId1),
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now()
        )

        val getSummaryResult = incomeCategoryRepository.getSummary(filter)
        getSummaryResult.shouldBeOk()
        getSummaryResult.onOk {
            it shouldBe expectedIncomeCategorySummary
        }
    }
}
