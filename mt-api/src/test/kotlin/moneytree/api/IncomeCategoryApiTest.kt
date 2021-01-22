package moneytree.api

import io.mockk.mockkClass
import java.util.UUID
import moneytree.domain.entity.IncomeCategory
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.IncomeCategoryRepository
import moneytree.validator.IncomeCategoryValidator

class IncomeCategoryApiTest : RoutesTest<IncomeCategory>() {

    private val randomUUID = UUID.randomUUID()
    private val randomString = randomString()

    override val entity = IncomeCategory(
        id = randomUUID,
        name = randomString,
    )

    override val entityRepository = mockkClass(IncomeCategoryRepository::class)
    override val entityValidator = IncomeCategoryValidator()
    override val entityApi = IncomeCategoryApi(entityRepository, entityValidator)

    override val entityPath = "/category/income"
}
