package moneytree.api

import io.mockk.every
import io.mockk.mockkClass
import java.util.UUID
import moneytree.domain.entity.ExpenseCategory
import moneytree.libs.commons.result.toOk
import moneytree.libs.http4k.buildRoutes
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.ExpenseCategoryRepository
import moneytree.validator.ExpenseCategoryValidator
import org.http4k.server.Jetty
import org.http4k.server.asServer

class ExpenseCategoryApiTest : BasicRoutesTest<ExpenseCategory>() {

    private val randomUUID = UUID.randomUUID()
    private val randomString = randomString()
    private val randomBigDecimal = randomBigDecimal()

    override val entity = ExpenseCategory(
        id = randomUUID,
        name = randomString,
        targetAmount = randomBigDecimal
    )

    override val entityRepository = mockkClass(ExpenseCategoryRepository::class)
    override val entityValidator = ExpenseCategoryValidator()
    override val entityApi = ExpenseCategoryApi(entityRepository, entityValidator)

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
        return "http://localhost:${server.port()}/category/expense"
    }
}
