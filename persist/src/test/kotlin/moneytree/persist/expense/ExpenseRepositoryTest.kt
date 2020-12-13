package moneytree.persist.expense

import PersistTestModule
import PersistTestModule.Companion.injector
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class ExpenseRepositoryTest : PersistTestModule() {

    private val expenseRepository = injector.getInstance(ExpenseRepository::class.java)

    @Test
    fun `test 1`() {
        print("ASFASDFASDFASDLKFJASFLK")
        1 shouldBe 1
    }

    @Test
    fun `test 2`() {
        2 shouldBe 2
    }
}
