package moneytree

import java.util.UUID
import moneytree.domain.Repository
import moneytree.libs.http4k.HttpRouting
import moneytree.validator.ValidationResult
import moneytree.validator.Validator
import moneytree.validator.validateUUID
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

abstract class MtApiRoutes<T>(
    private val repository: Repository<T>,
    private val validator: Validator<T>
) : HttpRouting<T> {

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

    override fun upsertById(request: Request): Response {
        val uuid = idLens(request)
        val updatedEntity = lens(request)

        return when (validator.validateWithUUID(updatedEntity, uuid)) {
            ValidationResult.Accepted -> processUpdateResult(
                repository.upsertById(
                    updatedEntity,
                    UUID.fromString(uuid)
                ),
                lens
            )
            ValidationResult.Rejected -> Response(Status.BAD_REQUEST)
        }
    }
}
