package moneytree.libs.test.commons

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class RandomTest {

    @Test
    fun `randomString generates a randomString`() {
        val randomString = randomString()

        randomString shouldNotBe ""
        randomString.length shouldBe 5
    }
}
