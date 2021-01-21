package moneytree.api

import io.mockk.every
import io.mockk.mockkClass
import java.time.LocalDate
import java.util.UUID
import moneytree.domain.entity.Income
import moneytree.libs.commons.result.toOk
import moneytree.libs.http4k.buildRoutes
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.IncomeRepository
import moneytree.validator.IncomeValidator
import org.http4k.server.Jetty
import org.http4k.server.asServer

class IncomeApiTest: BasicRoutesTest<Income>() {

    private val randomUUID = UUID.randomUUID()
    private val randomSource = randomString()
    private val todayLocalDate = LocalDate.now()
    private val incomeCategoryId = UUID.randomUUID()
    private val randomTransactionAmount = randomBigDecimal()
    private val notes = randomString()
    private val hide = false

    override val entity = Income(
        id = randomUUID,
        source = randomSource,
        incomeCategory = incomeCategoryId,
        transactionDate = todayLocalDate,
        transactionAmount = randomTransactionAmount,
        notes = notes,
        hide = hide
    )

    override val entityRepository = mockkClass(IncomeRepository::class)
    override val entityValidator = IncomeValidator()
    override val entityApi = IncomeApi(entityRepository, entityValidator)

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
        return "http://localhost:${server.port()}/income"
    }
}
