package moneytree.validator

import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.Income
import moneytree.libs.test.commons.randomBigDecimal
import moneytree.libs.test.commons.randomString
import org.junit.jupiter.api.Test

class IncomeValidatorTest {

    private val incomeValidator = IncomeValidator()

    @Test
    fun `validates valid income happy path`() {
        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val todayDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val randomIncomeCategoryId = UUID.randomUUID()
        val randomNotes = randomString()
        val hide = false

        val income = Income(
            id = randomUUID,
            source = randomSource,
            incomeCategory = randomIncomeCategoryId,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            notes = randomNotes,
            hide = hide
        )

        val result = incomeValidator.validate(income)

        result shouldBe ValidationResult.Accepted
    }

    @Test
    fun `rejects invalid income uuid`() {
        val randomUUID = UUID.randomUUID()
        val badUUID = UUID.fromString("11111111-1111-1111-8dda-111111111111")
        val randomSource = randomString()
        val todayDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val randomNotes = randomString()
        val hide = false

        val income = Income(
            id = badUUID,
            source = randomSource,
            incomeCategory = randomUUID,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            notes = randomNotes,
            hide = hide
        )

        val result = incomeValidator.validate(income)

        result shouldBe ValidationResult.Rejected
    }

    @Test
    fun `rejects invalid income category id`() {
        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val todayDate = LocalDate.now()
        val randomTransactionAmount = randomBigDecimal()
        val badUUID = UUID.fromString("11111111-1111-1111-8dda-111111111111")
        val randomNotes = randomString()
        val hide = false

        val income = Income(
            id = randomUUID,
            source = randomSource,
            incomeCategory = badUUID,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            notes = randomNotes,
            hide = hide
        )

        val result = incomeValidator.validate(income)

        result shouldBe ValidationResult.Rejected
    }

    @Test
    fun `rejects negative transaction`() {
        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val todayDate = LocalDate.now()
        val randomIncomeCategoryId = UUID.randomUUID()
        val randomTransactionAmount = BigDecimal.valueOf(-1L)
        val randomNotes = randomString()
        val hide = false

        val income = Income(
            id = randomUUID,
            source = randomSource,
            incomeCategory = randomIncomeCategoryId,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            notes = randomNotes,
            hide = hide
        )

        val result = incomeValidator.validate(income)

        result shouldBe ValidationResult.Rejected
    }

    @Test
    fun `rejects high transaction amount not supported by database schema`() {
        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val todayDate = LocalDate.now()
        val randomIncomeCategoryId = UUID.randomUUID()
        val randomTransactionAmount = BigDecimal.valueOf(Long.MAX_VALUE)
        val randomNotes = randomString()
        val hide = false

        val income = Income(
            id = randomUUID,
            source = randomSource,
            incomeCategory = randomIncomeCategoryId,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            notes = randomNotes,
            hide = hide
        )

        val result = incomeValidator.validate(income)

        result shouldBe ValidationResult.Rejected
    }

    @Test
    fun `rejects invalid notes input length`() {
        val randomUUID = UUID.randomUUID()
        val randomSource = randomString()
        val todayDate = LocalDate.now()
        val randomIncomeCategoryId = UUID.randomUUID()
        val randomTransactionAmount = BigDecimal.valueOf(Long.MAX_VALUE)
        val randomNotes = randomString(999)
        val hide = false

        val income = Income(
            id = randomUUID,
            source = randomSource,
            incomeCategory = randomIncomeCategoryId,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            notes = randomNotes,
            hide = hide
        )

        val result = incomeValidator.validate(income)

        result shouldBe ValidationResult.Rejected
    }

    @Test
    fun `rejects invalid source input length`() {
        val randomUUID = UUID.randomUUID()
        val randomSource = randomString(999)
        val todayDate = LocalDate.now()
        val randomIncomeCategoryId = UUID.randomUUID()
        val randomTransactionAmount = BigDecimal.valueOf(Long.MAX_VALUE)
        val randomNotes = randomString()
        val hide = false

        val income = Income(
            id = randomUUID,
            source = randomSource,
            incomeCategory = randomIncomeCategoryId,
            transactionDate = todayDate,
            transactionAmount = randomTransactionAmount,
            notes = randomNotes,
            hide = hide
        )

        val result = incomeValidator.validate(income)

        result shouldBe ValidationResult.Rejected
    }
}
