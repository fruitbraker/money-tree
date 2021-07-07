package moneytree.validator

import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.domain.entity.Vendor
import moneytree.libs.testcommons.randomString
import moneytree.validator.ValidationResult.Accepted
import moneytree.validator.ValidationResult.Rejected
import org.junit.jupiter.api.Test

class VendorValidatorTest {
    private val validator = VendorValidator()

    @Test
    fun `accepts valid vendor input`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()

        val randomVendor = Vendor(
            vendorId = randomUUID,
            name = randomString
        )

        val result = validator.validate(randomVendor)

        result shouldBe Accepted
    }

    @Test
    fun `rejects invalid vendor input`() {
        val randomUUID = UUID.randomUUID()
        val invalidString = randomString(999)

        val randomVendor = Vendor(
            vendorId = randomUUID,
            name = invalidString
        )

        val result = validator.validate(randomVendor)

        result shouldBe Rejected
    }
}
