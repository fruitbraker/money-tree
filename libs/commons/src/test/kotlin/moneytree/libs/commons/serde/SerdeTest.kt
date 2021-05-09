package moneytree.libs.commons.serde

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import moneytree.libs.testcommons.randomString
import org.junit.jupiter.api.Test

class SerdeTest {
    private data class Sample(
        @JsonDeserialize(using = TrimStringDeserializer::class)
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

    @Test
    fun `Uses custom deserializer to trim whitespaces in string`() {
        val randomString = randomString()

        val sample = Sample(
            string = "     $randomString    ",
            int = 1,
            list = listOf<Int>()
        )

        val expectedSample = sample.copy(
            string = randomString
        )

        val result = jackson.readValue<Sample>(sample.toJson())
        result shouldBe expectedSample
    }
}
