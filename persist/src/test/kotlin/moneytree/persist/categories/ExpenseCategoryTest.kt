package moneytree.persist.categories

import PersistTestModule
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class ExpenseCategoryTest : PersistTestModule() {

    @Test
    fun `test 1`() {
        1 shouldBe 1
    }

    @Test
    fun `test 2`() {
        2 shouldBe 2
    }
}
