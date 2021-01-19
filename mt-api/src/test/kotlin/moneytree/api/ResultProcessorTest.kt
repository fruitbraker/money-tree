package moneytree.api

import io.kotest.matchers.shouldBe
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.testcommons.randomString
import moneytree.processGetByIdResult
import moneytree.processGetResult
import moneytree.processInsertResult
import org.http4k.core.Body
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.junit.jupiter.api.Test

class ResultProcessorTest {

    private val lens = Body.auto<Sample>().toLens()
    private val listLens = Body.auto<List<Sample>>().toLens()

    @Test
    fun `processGetResult returns OK with a list of entity`() {
        val listSample = listOf(
            Sample(randomString()),
            Sample(randomString())
        )
        val okResult = listSample.toOk()

        val result = processGetResult(okResult, listLens)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listSample.toJson()
    }

    @Test
    fun `processGetResult returns with OK with empty list`() {
        val listSample = emptyList<Sample>()
        val okResult = listSample.toOk()

        val result = processGetResult(okResult, listLens)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listSample.toJson()
    }

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
