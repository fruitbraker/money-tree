package moneytree.validator

import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.Expense
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import org.junit.jupiter.api.Test

class ExpenseValidatorTest {

    private val expenseValidator = ExpenseValidator()

    @Test
    fun `validates valid expense happy path`() {
        val randomUUID = UUID.randomUUID()
        val todayDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val randomVendorId = UUID.randomUUID()
        val randomExpenseCategoryId = UUID.randomUUID()
        val randomNotes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            vendorId = randomVendorId,
            expenseCategoryId = randomExpenseCategoryId,
            notes = randomNotes,
            hide = hide
        )

        val result = expenseValidator.validate(expense)

        result shouldBe ValidationResult.Accepted
    }

    @Test
    fun `rejects invalid vendor uuid`() {
        val randomUUID = UUID.randomUUID()
        val todayDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val badUUID = UUID.fromString("11111111-1111-1111-8dda-111111111111")
        val randomExpenseCategoryId = UUID.randomUUID()
        val randomNotes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            vendorId = badUUID,
            expenseCategoryId = randomExpenseCategoryId,
            notes = randomNotes,
            hide = hide
        )

        val result = expenseValidator.validate(expense)

        result shouldBe ValidationResult.Rejected
    }

    @Test
    fun `rejects invalid expense category uuid`() {
        val randomUUID = UUID.randomUUID()
        val todayDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val badUUID = UUID.fromString("11111111-1111-1111-8dda-111111111111")
        val vendorId = UUID.randomUUID()
        val randomNotes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            vendorId = vendorId,
            expenseCategoryId = badUUID,
            notes = randomNotes,
            hide = hide
        )

        val result = expenseValidator.validate(expense)

        result shouldBe ValidationResult.Rejected
    }

    @Test
    fun `rejects negative transaction amount`() {
        val randomUUID = UUID.randomUUID()
        val todayDate = LocalDate.now()
        val randomTransactionAmount = BigDecimal.valueOf(-1L)
        val randomVendorId = UUID.randomUUID()
        val randomExpenseCategoryId = UUID.randomUUID()
        val randomNotes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            vendorId = randomVendorId,
            expenseCategoryId = randomExpenseCategoryId,
            notes = randomNotes,
            hide = hide
        )

        val result = expenseValidator.validate(expense)

        result shouldBe ValidationResult.Rejected
    }

    @Test
    fun `rejects high transaction amount that isn't supported by schema defined in database`() {
        val randomUUID = UUID.randomUUID()
        val todayDate = LocalDate.now()
        val randomTransactionAmount = BigDecimal.valueOf(Long.MAX_VALUE)
        val randomVendorId = UUID.randomUUID()
        val randomExpenseCategoryId = UUID.randomUUID()
        val randomNotes = randomString()
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            vendorId = randomVendorId,
            expenseCategoryId = randomExpenseCategoryId,
            notes = randomNotes,
            hide = hide
        )

        val result = expenseValidator.validate(expense)

        result shouldBe ValidationResult.Rejected
    }

    @Test
    fun `rejects invalid notes input`() {
        val randomUUID = UUID.randomUUID()
        val todayDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val randomVendorId = UUID.randomUUID()
        val randomExpenseCategoryId = UUID.randomUUID()
        val randomNotes = randomString(999)
        val hide = false

        val expense = Expense(
            id = randomUUID,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            vendorId = randomVendorId,
            expenseCategoryId = randomExpenseCategoryId,
            notes = randomNotes,
            hide = hide
        )

        val result = expenseValidator.validate(expense)

        result shouldBe ValidationResult.Rejected
    }
}
