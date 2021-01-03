package moneytree.api

import io.kotest.matchers.shouldBe
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.test.commons.randomString
import org.http4k.core.Body
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.junit.jupiter.api.Test

class ResultProcessorTest {

    private val lens = Body.auto<Sample>().toLens()

    @Test
    fun `processGetByIdResult returns ok with nonnull entity`() {
        val sample = Sample(randomString())
        val okResult = sample.toOk()

        val result = processGetByIdResult(okResult, lens)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe sample.toJson()
    }

    @Test
    fun `processGetByIdResult returns not found with null entity`() {
        val sample: Sample? = null
        val okResult = sample.toOk()

        val result = processGetByIdResult(okResult, lens)

        result.status shouldBe Status.NOT_FOUND
    }

    @Test
    fun `processInsertResult returns created onOk`() {
        val sample = Sample(randomString())
        val okResult = sample.toOk()

        val result = processInsertResult(okResult, lens)

        result.status shouldBe Status.CREATED
        result.bodyString() shouldBe sample.toJson()
    }

    private data class Sample(
        val blah: String
    )
}
