package moneytree.libs.http4k

import io.kotest.matchers.shouldBe
import moneytree.libs.testcommons.randomString
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
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

    @Test
    fun `adding additional routes happy path`() {
        val randomPath = randomString()

        val newRoute = listOf<RoutingHttpHandler>(
            "/$randomPath" bind Method.GET to {
                Response(Status.OK).body("test")
            }
        )

        buildRoutes(newRoute).getServer().useTestServer {
            val result = client(Request(Method.GET, "http://localhost:${it.port()}/$randomPath"))

            result.status shouldBe Status.OK
            result.bodyString() shouldBe "test"
        }
    }
}
