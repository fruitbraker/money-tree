package moneytree.filter

import io.kotest.matchers.collections.shouldContain
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.junit.jupiter.api.Test

class RateLimiterFilterTest {

    @Test
    fun `Rate limits`() {
        // This is so ugly. I'm sorry. There's ticket to look at this again.

        val handler = rateLimiterFilter.then { Response(Status.OK) }

        val statusCodes = mutableListOf<Status>()
        for (i in 1..100) {
            val response = handler(
                Request(Method.GET, "/blah")
            )

            statusCodes.add(response.status)
        }

        statusCodes shouldContain Status.TOO_MANY_REQUESTS
    }
}
