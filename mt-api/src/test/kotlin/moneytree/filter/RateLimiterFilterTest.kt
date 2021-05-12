package moneytree.filter

import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then

@OptIn(ExperimentalTime::class)
class RateLimiterFilterTest : ShouldSpec({
    should("rate limit handler") {
        val handler = rateLimiterFilter.then { Response(Status.OK) }

        eventually(Duration.seconds(1)) {
            println("Hi")
            val response = handler(
                Request(Method.GET, "/blah")
            )
            response.status shouldBe Status.TOO_MANY_REQUESTS
        }
    }
})
