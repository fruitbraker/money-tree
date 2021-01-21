package moneytree.api

import io.kotest.matchers.shouldBe
import moneytree.libs.commons.result.toErr
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.testcommons.randomString
import moneytree.persist.FOREIGN_KEY_CONSTRAINT_VIOLATION
import moneytree.processGetByIdResult
import moneytree.processGetResult
import moneytree.processInsertResult
import moneytree.processUpsertResult
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
    fun `processGetResult returns bad request on error result`() {
        val errResult = SampleThrowable().toErr()

        val result = processGetResult(errResult, listLens)

        result.status shouldBe Status.BAD_REQUEST
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
    fun `processGetByIdResult returns bad request on error result`() {
        val errResult = SampleThrowable().toErr()

        val result = processGetByIdResult(errResult, lens)

        result.status shouldBe Status.BAD_REQUEST
    }

    @Test
    fun `processInsertResult returns created onOk`() {
        val sample = Sample(randomString())
        val okResult = sample.toOk()

        val result = processInsertResult(okResult, lens)

        result.status shouldBe Status.CREATED
        result.bodyString() shouldBe sample.toJson()
    }

    @Test
    fun `processInsertResult returns bad request on error result`() {
        val errResult = SampleThrowable().toErr()

        val result = processInsertResult(errResult, lens)

        result.status shouldBe Status.BAD_REQUEST
    }

    @Test
    fun `processUpsertResult returns ok happy path`() {
        val sample = Sample(randomString())
        val okResult = sample.toOk()

        val result = processUpsertResult(okResult, lens)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe sample.toJson()
    }

    @Test
    fun `processUpsertResult returns bad request on error result`() {
        val errResult = SampleThrowable().toErr()

        val result = processUpsertResult(errResult, lens)

        result.status shouldBe Status.BAD_REQUEST
    }

    @Test
    fun `processUpsertResult returns conflict on foreign key constraint violation`() {
        val errResult = SampleThrowable(FOREIGN_KEY_CONSTRAINT_VIOLATION).toErr()

        val result = processUpsertResult(errResult, lens)

        result.status shouldBe Status.CONFLICT
    }

    private data class Sample(
        val blah: String
    )

    private class SampleThrowable(reason: String = randomString()) : Throwable(reason)
}
