package moneytree.api.expense

import io.kotest.matchers.shouldBe
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Test

class ExpenseApiTest {

    companion object {
        const val GET_URL = "http://any/expense"
    }

    @Test
    fun `Expense get happy path`() {
        val expenseApi = ExpenseApi()

        val request = Request(
            Method.GET,
            GET_URL
        )

        val result = expenseApi.get(request)

        result.status shouldBe Status.OK
    }
}
