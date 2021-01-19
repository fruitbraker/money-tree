package moneytree.validator

import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.util.UUID
import moneytree.domain.entity.ExpenseCategory
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import org.junit.jupiter.api.Test

class ExpenseCategoryValidatorTest {
    private val expenseCategoryValidator = ExpenseCategoryValidator()

    @Test
    fun `accepts proper ExpenseCategory entity`() {
        val randomUUID = UUID.randomUUID()
        val randomName = randomString()
        val randomTargetAmount = randomBigDecimal()

        val expenseCategory = ExpenseCategory(
            id = randomUUID,
            name = randomName,
            targetAmount = randomTargetAmount
        )

        val result = expenseCategoryValidator.validate(expenseCategory)

        result shouldBe ValidationResult.Accepted
    }

    @Test
    fun `rejects improper name length`() {
        val randomUUID = UUID.randomUUID()
        val randomName = randomString(999)
        val randomTargetAmount = randomBigDecimal()

        val expenseCategory = ExpenseCategory(
            id = randomUUID,
            name = randomName,
            targetAmount = randomTargetAmount
        )

        val result = expenseCategoryValidator.validate(expenseCategory)

        result shouldBe ValidationResult.Rejected
    }

    @Test
    fun `rejects improper target amount`() {
        val randomUUID = UUID.randomUUID()
        val randomName = randomString()
        val randomTargetAmount = BigDecimal(Double.MAX_VALUE)

        val expenseCategory = ExpenseCategory(
            id = randomUUID,
            name = randomName,
            targetAmount = randomTargetAmount
        )

        val result = expenseCategoryValidator.validate(expenseCategory)

        result shouldBe ValidationResult.Rejected
    }
}
