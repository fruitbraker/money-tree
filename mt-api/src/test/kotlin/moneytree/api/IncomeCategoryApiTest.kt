package moneytree.api

import io.mockk.every
import io.mockk.mockkClass
import java.util.UUID
import moneytree.domain.entity.IncomeCategory
import moneytree.libs.commons.result.toOk
import moneytree.libs.http4k.buildRoutes
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.IncomeCategoryRepository
import moneytree.validator.IncomeCategoryValidator
import org.http4k.server.Jetty
import org.http4k.server.asServer

class IncomeCategoryApiTest : BasicRoutesTest<IncomeCategory>() {

    private val randomUUID = UUID.randomUUID()
    private val randomString = randomString()

    override val entity = IncomeCategory(
        id = randomUUID,
        name = randomString,
    )

    override val entityRepository = mockkClass(IncomeCategoryRepository::class)
    override val entityValidator = IncomeCategoryValidator()
    override val entityApi = IncomeCategoryApi(entityRepository, entityValidator)

    override val server = buildRoutes(
        listOf(
            entityApi.makeRoutes()
        )
    ).asServer(Jetty(0))

    override val url = setup()

    override fun setup(): String {
        every { entityRepository.get() } returns listOf(entity).toOk()
        every { entityRepository.getById(any()) } returns entity.toOk()
        every { entityRepository.insert(entity) } returns entity.toOk()
        every { entityRepository.upsertById(entity, any()) } returns entity.toOk()

        server.start()
        return "http://localhost:${server.port()}/category/income"
    }
}
