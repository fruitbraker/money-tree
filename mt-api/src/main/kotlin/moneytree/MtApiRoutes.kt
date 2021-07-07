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
    val repository: Repository<T>,
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
        val newEntity = try {
            lens(request)
        } catch (e: Throwable) {
            return Response(Status.BAD_REQUEST)
        }
        return when (validator.validate(newEntity)) {
            ValidationResult.Accepted -> {
                processInsertResult(repository.insert(newEntity), lens)
            }
            ValidationResult.Rejected -> Response(Status.BAD_REQUEST)
        }
    }

    override fun upsertById(request: Request): Response {
        val uuid = idLens(request)
        val updatedEntity = try {
            lens(request)
        } catch (e: Throwable) {
            return Response(Status.BAD_REQUEST)
        }

        return when (validator.validateWithUUID(updatedEntity, uuid)) {
            ValidationResult.Accepted -> processUpsertResult(
                repository.upsertById(
                    updatedEntity,
                    UUID.fromString(uuid)
                ),
                lens
            )
            ValidationResult.Rejected -> Response(Status.BAD_REQUEST)
        }
    }

    override fun deleteById(request: Request): Response {
        val uuid = idLens(request)

        return when (uuid.validateUUID()) {
            ValidationResult.Accepted -> processDeleteByIdResult(repository.deleteById(UUID.fromString(uuid)))
            ValidationResult.Rejected -> Response(Status.BAD_REQUEST)
        }
    }
}
