package moneytree.api.contracts

import io.kotest.matchers.shouldBe
import io.mockk.every
import moneytree.domain.SummaryRepository
import moneytree.domain.entity.ExpenseSummaryFilter
import moneytree.domain.entity.Filter
import moneytree.libs.commons.result.toOk
import moneytree.libs.commons.serde.toJson
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

abstract class RoutesWithSummaryTest<T, S, F : Filter> : RoutesTest<T>() {

    abstract val entitySummary: S
    abstract val entitySummaryRepository: SummaryRepository<S, F>

    abstract val entitySummaryPath: String

    abstract val filter: F

    abstract val summaryUrl: String

    @BeforeAll
    abstract override fun start()

    @Test
    abstract fun `getSummary happy path`()

    @Test
    abstract fun `getSummaryById happy path`()
}
