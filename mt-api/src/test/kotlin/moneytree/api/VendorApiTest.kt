package moneytree.api

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import java.util.UUID
import moneytree.domain.Vendor
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import moneytree.libs.http4k.buildRoutes
import moneytree.libs.test.commons.randomString
import moneytree.persist.VendorRepository
import moneytree.validator.VendorValidator
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VendorApiTest {
    private val client = OkHttp()

    private val randomUUID = UUID.randomUUID()
    private val randomString = randomString()

    private val vendor = Vendor(
        id = randomUUID,
        name = randomString
    )

    private val vendorRepository = mockkClass(VendorRepository::class)
    private val vendorValidator = VendorValidator()
    private val vendorApi = VendorApi(vendorRepository, vendorValidator)

    private val server = buildRoutes(
        listOf(
            vendorApi.makeRoutes()
        )
    ).asServer(Jetty(0))

    private val url = setup()

    private fun setup(): String {
        every { vendorRepository.get() } returns listOf(vendor).toOk()
        every { vendorRepository.getById(randomUUID) } returns vendor.toOk()
        every { vendorRepository.insert(vendor) } returns vendor.toOk()
        every { vendorRepository.upsertById(vendor, randomUUID) } returns vendor.toOk()

        server.start()
        return "http://localhost:${server.port()}/vendor"
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
            url
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe listOf(vendor).toJson()
    }

    @Test
    fun `getById happy path`() {
        val request = Request(
            Method.GET,
            "$url/$randomUUID"
        )

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe vendor.toJson()
    }

    @Test
    fun `insert happy path`() {
        val request = Request(
            Method.POST,
            url
        ).with(vendorApi.lens of vendor)

        val result = client(request)

        result.status shouldBe Status.CREATED
        result.bodyString() shouldBe vendor.toJson()
    }

    @Test
    fun `updateById happy path`() {
        val request = Request(
            Method.PUT,
            "$url/$randomUUID"
        ).with(vendorApi.lens of vendor)

        val result = client(request)

        result.status shouldBe Status.OK
        result.bodyString() shouldBe vendor.toJson()
    }
}
