package moneytree.api

import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.libs.commons.serde.toJson
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Test

abstract class RoutesWithSummaryTest<T, S> : RoutesTest<T>() {
    private val randomUUID = UUID.randomUUID()

    abstract val entitySummary: S

    @Test
    fun `getSummary happy path`() {
        val request = Request(
            Method.GET,
            "$url/summary"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(entitySummary).toJson()
    }

    @Test
    fun `getSummaryById happy path`() {
        val request = Request(
            Method.GET,
            "$url/summary/$randomUUID"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe entitySummary?.toJson()
    }
}
