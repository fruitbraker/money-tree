package moneytree.api

import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.domain.Repository
import moneytree.libs.commons.serde.toJson
import moneytree.libs.http4k.HttpRouting
import moneytree.validator.Validator
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BasicRoutesTest<T> {

    val client = OkHttp()

    private val randomUUID = UUID.randomUUID()

    abstract val entity: T
    abstract val entityRepository: Repository<T>
    abstract val entityValidator: Validator<T>
    abstract val entityApi: HttpRouting<T>
    abstract val server: Http4kServer

    abstract fun setup(): String

    abstract val url: String

    @AfterAll
    fun cleanUp() {
        server.stop()
        client.close()
    }

    @Test
    fun `get happy path`() {
        val request = Request(
            Method.GET,
            url
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(entity).toJson()
    }

    @Test
    fun `getById happy path`() {
        val request = Request(
            Method.GET,
            "$url/$randomUUID"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe entity?.toJson()
    }

    @Test
    fun `insert happy path`() {
        val request = Request(
            Method.POST,
            url
        ).with(entityApi.lens of entity)

        val result = client(request)

        result.status shouldBe Status.CREATED
        result.bodyString() shouldBe entity?.toJson()
    }

    @Test
    fun `updateById happy path`() {
        val request = Request(
            Method.PUT,
            "$url/$randomUUID"
        ).with(entityApi.lens of entity)

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe entity?.toJson()
    }
}
