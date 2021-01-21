package moneytree

import java.util.UUID
import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.validator.ValidationResult
import moneytree.validator.Validator
import moneytree.validator.validateUUID
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.BiDiBodyLens

abstract class MtApiRoutesWithSummary<T, S>(
    private val repository: Repository<T>,
    private val validator: Validator<T>
): MtApiRoutes<T>(
    repository,
    validator
) {
    abstract val summaryRepository: SummaryRepository<S>
    abstract val summaryLens: BiDiBodyLens<S>
    abstract val summaryListLens: BiDiBodyLens<List<S>>

    @Suppress("UNUSED_PARAMETER")
    fun getSummary(request: Request): Response {
        return processGetResult(summaryRepository.getSummary(), summaryListLens)
    }

    fun getSummaryById(request: Request): Response {
        val uuid = idLens(request)
        return when (uuid.validateUUID()) {
            ValidationResult.Accepted -> processGetByIdResult(
                summaryRepository.getSummaryById(
                    UUID.fromString(
                        uuid
                    )
                ),
                summaryLens
            )
            ValidationResult.Rejected -> Response(Status.BAD_REQUEST)
        }
    }
}
