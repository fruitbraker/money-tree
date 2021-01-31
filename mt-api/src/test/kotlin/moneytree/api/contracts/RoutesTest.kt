package moneytree.api.contracts

import io.kotest.matchers.shouldBe
import io.mockk.every
import java.util.UUID
import moneytree.domain.Repository
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.http4k.HttpRouting
import moneytree.libs.http4k.buildRoutes
import moneytree.validator.Validator
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class RoutesTest<T> {

    internal val client = OkHttp()

    internal val randomUUIDParameter = UUID.randomUUID()

    abstract val entity: T
    abstract val entityRepository: Repository<T>
    abstract val entityValidator: Validator<T>
    abstract val entityApi: HttpRouting<T>

    private var _server: Http4kServer? = null
    internal val server
        get() = _server ?: throw IllegalStateException("Server cannot be null!")

    internal val url
        get() = "http://localhost:${server.port()}"

    abstract val entityPath: String

    @BeforeAll
    open fun start() {
        every { entityRepository.get() } returns listOf(entity).toOk()
        every { entityRepository.getById(any()) } returns entity.toOk()
        every { entityRepository.insert(entity) } returns entity.toOk()
        every { entityRepository.upsertById(entity, any()) } returns entity.toOk()
        every { entityRepository.deleteById(any()) } returns Unit.toOk()

        _server = buildRoutes(
            listOf(
                entityApi.makeRoutes()
            )
        ).asServer(Jetty(0))

        server.start()
    }

    @AfterAll
    fun cleanUp() {
        server.stop()
        client.close()
    }

    @Test
    fun `get happy path`() {
        val request = Request(
            Method.GET,
            "$url$entityPath"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(entity).toJson()
    }

    @Test
    fun `getById happy path`() {
        val request = Request(
            Method.GET,
            "$url$entityPath/$randomUUIDParameter"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe entity?.toJson()
    }

    @Test
    fun `insert happy path`() {
        val request = Request(
            Method.POST,
            "$url$entityPath"
        ).with(entityApi.lens of entity)

        val result = client(request)

        result.status shouldBe Status.CREATED
        result.bodyString() shouldBe entity?.toJson()
    }

    @Test
    fun `updateById happy path`() {
        val request = Request(
            Method.PUT,
            "$url$entityPath/$randomUUIDParameter"
        ).with(entityApi.lens of entity)

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe entity?.toJson()
    }

    @Test
    fun `deleteById happy path`() {
        val request = Request(
            Method.DELETE,
            "$url$entityPath/$randomUUIDParameter"
        ).with(entityApi.lens of entity)

        val result = client(request)

        result.status shouldBe Status.NO_CONTENT
    }
}
