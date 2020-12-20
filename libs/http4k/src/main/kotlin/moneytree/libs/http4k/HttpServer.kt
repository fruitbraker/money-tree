package moneytree.libs.http4k

import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

private val healthCheck = "/health" bind Method.GET to {
    Response(Status.OK).body("Money tree is healthy.")
}

fun buildRoutes(
    routes: List<RoutingHttpHandler> = emptyList()
): RoutingHttpHandler = routes(
    healthCheck,
    *routes.toTypedArray()
)

fun RoutingHttpHandler.getServer(): Http4kServer {
    val server = Jetty(0)
    return this.asServer(server)
}

// For testing purposes
internal fun Http4kServer.useTestServer(block: (Http4kServer) -> Unit) {
    this.use { testServer ->
        testServer.start()
        block(testServer)
    }
}
