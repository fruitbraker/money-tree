package moneytree.validator

import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.libs.test.commons.randomString
import org.junit.jupiter.api.Test

class UUIDValidatorTest {

    @Test
    fun `accepts a valid v4 uuid structure`() {
        val uuid = UUID.randomUUID().toString()
        val result = uuid.validateUUID()

        result shouldBe ValidationResult.Accepted
    }

    @Test
    fun `rejects an invalid uuid structure`() {
        val badUUID = "${randomString()}-${randomString()}"
        val result = badUUID.validateUUID()

        result shouldBe ValidationResult.Rejected
    }
}
