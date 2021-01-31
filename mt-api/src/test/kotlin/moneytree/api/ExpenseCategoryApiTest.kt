package moneytree.api

import io.mockk.mockkClass
import java.util.UUID
import moneytree.api.contracts.RoutesTest
import moneytree.domain.entity.ExpenseCategory
import moneytree.libs.testcommons.randomBigDecimal
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.ExpenseCategoryRepository
import moneytree.validator.ExpenseCategoryValidator

class ExpenseCategoryApiTest : RoutesTest<ExpenseCategory>() {

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

    override val entityPath: String = "/category/expense"
}
