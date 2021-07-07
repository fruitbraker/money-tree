package moneytree.validator

import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.domain.entity.IncomeCategory
import moneytree.libs.testcommons.randomString
import org.junit.jupiter.api.Test

class IncomeCategoryValidatorTest {
    private val incomeCategoryValidator = IncomeCategoryValidator()

    @Test
    fun `accepts proper IncomeCategory entity`() {
        val randomUUID = UUID.randomUUID()
        val randomName = randomString()

        val incomeCategory = IncomeCategory(
            incomeCategoryId = randomUUID,
            name = randomName
        )

        val result = incomeCategoryValidator.validate(incomeCategory)

        result shouldBe ValidationResult.Accepted
    }

    @Test
    fun `rejects improper name length`() {
        val randomUUID = UUID.randomUUID()
        val randomName = randomString(999)

        val incomeCategory = IncomeCategory(
            incomeCategoryId = randomUUID,
            name = randomName
        )

        val result = incomeCategoryValidator.validate(incomeCategory)

        result shouldBe ValidationResult.Rejected
    }
}
