package moneytree.libs.http4k

import io.kotest.matchers.shouldBe
import moneytree.libs.test.common.randomString
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Test

class Http4kTest {

    private val client = OkHttp()

    @Test
    fun `health check happy path`() {
        buildRoutes().getServer().useTestServer {
            val result = client(Request(Method.GET, "http://localhost:${it.port()}/health"))

            result.status shouldBe Status.OK
            result.bodyString() shouldBe "Money tree is healthy."
        }
    }

    @Test
    fun `unknown path should return NOT FOUND`() {
        buildRoutes().getServer().useTestServer {
            val result = client(Request(Method.GET, "http://localhost:${it.port()}/${randomString()}"))

            result.status shouldBe Status.NOT_FOUND
            result.bodyString() shouldBe ""
        }
    }
}
