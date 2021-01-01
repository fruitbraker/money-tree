package moneytree.libs.commons.result

import io.kotest.matchers.shouldBe
import moneytree.libs.test.commons.randomString
import org.junit.jupiter.api.Test

class ResultTest {

    @Test
    fun `toOk wraps value inside Result`() {
        val randomString = randomString()
        val result = randomString.toOk()

        result shouldBe Result.Ok(randomString)
        result.value shouldBe randomString
    }

    @Test
    fun `toErr wraps error inside Result`() {
        val exception = Exception()
        val result = exception.toErr()

        result shouldBe Result.Err(exception)
        result.error shouldBe exception
    }
}
