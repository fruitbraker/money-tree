package moneytree.libs.serde

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class SerdeTest {
    private data class Sample(
        val string: String,
        val int: Int,
        val list: List<*>
    )

    @Test
    fun `Can serialize and deserialize object`() {
        jackson shouldNotBe null

        val sample = Sample(
            string = "String",
            int = 1,
            list = listOf<Int>()
        )

        val sampleJson = jackson.writeValueAsString(sample)
        sampleJson shouldBe "{\"string\":\"String\",\"int\":1,\"list\":[]}"

        val sampleFromJson = jackson.readValue<Sample>(sampleJson)
        sampleFromJson shouldBe sample
    }
}
