package moneytree.api

import java.util.UUID
import moneytree.domain.ExpenseCategory
import moneytree.domain.Repository
import moneytree.libs.http4k.HttpRouting
import moneytree.validator.ValidationResult
import moneytree.validator.Validator
import moneytree.validator.validateUUID
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

class ExpenseCategoryApi(
    private val repository: Repository<ExpenseCategory>,
    private val validator: Validator<ExpenseCategory>
) : HttpRouting<ExpenseCategory> {

    override val lens: BiDiBodyLens<ExpenseCategory>
        get() = Body.auto<ExpenseCategory>().toLens()
    override val listLens: BiDiBodyLens<List<ExpenseCategory>>
        get() = Body.auto<List<ExpenseCategory>>().toLens()

    override fun makeRoutes(): RoutingHttpHandler {
        return routes(
            "/expense-category" bind Method.GET to this::get,
            "/expense-category/{id}" bind Method.GET to this::getById,
            "/expense-category" bind Method.POST to this::insert
        )
    }

    override fun get(request: Request): Response {
        return processGetResult(repository.get(), listLens)
    }

    override fun getById(request: Request): Response {
        val uuid = idLens(request)
        return when (uuid.validateUUID()) {
            ValidationResult.Accepted -> processGetByIdResult(repository.getById(UUID.fromString(uuid)), lens)
            ValidationResult.Rejected -> Response(Status.NOT_FOUND)
        }
    }

    override fun insert(request: Request): Response {
        val newEntity = lens(request)
        return when (validator.validate(newEntity)) {
            ValidationResult.Accepted -> {
                processInsertResult(repository.insert(newEntity), lens).status(Status.CREATED)
            }
            ValidationResult.Rejected -> Response(Status.BAD_REQUEST)
        }
    }
}
