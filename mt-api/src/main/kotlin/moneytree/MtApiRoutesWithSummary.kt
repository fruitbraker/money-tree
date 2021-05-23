package moneytree

import moneytree.domain.Repository
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.Filter
import moneytree.validator.Validator
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.lens.BiDiBodyLens

abstract class MtApiRoutesWithSummary<T, S, F : Filter>(
    repository: Repository<T>,
    validator: Validator<T>
) : MtApiRoutes<T>(
    repository,
    validator
) {
    abstract val summaryRepository: SummaryRepository<S, F>
    abstract val summaryListLens: BiDiBodyLens<List<S>>

    abstract fun getSummary(request: Request): Response
}
